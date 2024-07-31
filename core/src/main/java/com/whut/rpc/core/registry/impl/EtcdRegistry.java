package com.whut.rpc.core.registry.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.whut.rpc.core.config.RegistryConfig;
import com.whut.rpc.core.model.ServiceMetaInfo;
import com.whut.rpc.core.registry.BasicRegistry;
import com.whut.rpc.core.registry.RegistryServiceCache;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * the etcd implementation for registry center client
 *
 * @author whut2024
 * @since 2024-07-26
 */

@Data
@Slf4j
public class EtcdRegistry implements BasicRegistry {


    private Client client;


    private KV kvClient;


    private final String ETCD_ROOT_PATH = "/rpc/";


    /**
     * local registry key cache
     */
    private final Set<String> NODE_KEY_SET = new HashSet<>();


    /**
     * cache registered service
     */
    private final RegistryServiceCache SERVICE_CACHE = new RegistryServiceCache();


    /**
     * store watched node keys
     */
    private final Set<String> WATCHED_KEY_SET = new ConcurrentHashSet<>();


    @Override
    public void init(RegistryConfig config) {
        ClientBuilder builder = Client.builder().endpoints(config.getAddress()).connectTimeout(Duration.ofMillis(config.getTimeout()));

        if (StrUtil.isAllNotBlank(config.getUsername(), config.getPassword())) {
            builder
                    .user(ByteSequence.from(config.getUsername(), StandardCharsets.UTF_8))
                    .password(ByteSequence.from(config.getPassword(), StandardCharsets.UTF_8));
        }

        client = builder.build();

        kvClient = client.getKVClient();

        // start heart beat
        refresh();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        // store the service
        store(serviceMetaInfo, 30);

        // store the key
        String registryKey = ETCD_ROOT_PATH + serviceMetaInfo.getNodeKey();
        NODE_KEY_SET.add(registryKey);


        // delete the tool key and put the tool key
        ByteSequence toolKey = ByteSequence.from(getToolKey(registryKey), StandardCharsets.UTF_8);
        // delete will be watched and consumer terminal will clean the total service which has the same basic service class with the tool key service
        kvClient.delete(toolKey).get();
        // put it again
        kvClient.put(toolKey, ByteSequence.EMPTY);
    }

    @Override
    public void unregister(ServiceMetaInfo serviceMetaInfo) {
        String registryKey = ETCD_ROOT_PATH + serviceMetaInfo.getNodeKey();
        ByteSequence key = ByteSequence.from(registryKey, StandardCharsets.UTF_8);
        kvClient.delete(key);

        // remove registry key
        NODE_KEY_SET.remove(registryKey);
    }

    @Override
    public List<ServiceMetaInfo> discovery(String serviceKey) {
        // check cache
        List<ServiceMetaInfo> cacheList = SERVICE_CACHE.readCache(serviceKey);
        if (CollectionUtil.isNotEmpty(cacheList)) return cacheList;

        // prefix key
        String prefixKey = ETCD_ROOT_PATH + serviceKey + "/";

        // get meta data
        GetOption getOption = GetOption.builder().isPrefix(true).build();
        try {
            List<KeyValue> keyValueList = kvClient.get(ByteSequence.from(prefixKey, StandardCharsets.UTF_8), getOption).get().getKvs();

            if (CollectionUtil.isEmpty(keyValueList)) return Collections.emptyList();

            List<ServiceMetaInfo> serviceMetaInfoList = keyValueList.stream()
                    .filter(keyValue -> {
                        String key = keyValue.getKey().toString(StandardCharsets.UTF_8);

                        // pass the tool key
                        return key.charAt(key.length() - 1) != 'l';
                    })
                    .map(keyValue -> {
                        String key = keyValue.getKey().toString(StandardCharsets.UTF_8);

                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);

                        // watch the service at consumer terminal
                        watch(key);

                        return JSONUtil.toBean(value, ServiceMetaInfo.class);
                    }).collect(Collectors.toList());

            // cache it
            SERVICE_CACHE.writeCache(serviceKey, serviceMetaInfoList);

            // watch a tool key
            String singleKey = keyValueList.get(0).getKey().toString(StandardCharsets.UTF_8);


            kvClient.put(ByteSequence.from(getToolKey(singleKey), StandardCharsets.UTF_8), ByteSequence.EMPTY).get();

            return serviceMetaInfoList;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("discovery service failed");
        }
    }

    @Override
    public void destroy() {
        for (String registryKey : NODE_KEY_SET) {
            try {
                kvClient.delete(ByteSequence.from(registryKey, StandardCharsets.UTF_8)).get();
                kvClient.delete(ByteSequence.from(getToolKey(registryKey), StandardCharsets.UTF_8)).get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("off line service failed");
                throw new RuntimeException(e);
            }
        }


        log.warn("etcd registry client is going to close");

        if (kvClient != null) kvClient.close();

        if (client != null) client.close();

    }

    @Override
    public void watch(String nodeKey) {
        if (WATCHED_KEY_SET.contains(nodeKey)) return;

        Watch watchClient = client.getWatchClient();

        ByteSequence key = ByteSequence.from(nodeKey, StandardCharsets.UTF_8);
        watchClient.watch(key, response -> {
            for (WatchEvent event : response.getEvents()) {
                switch (event.getEventType()) {
                    case DELETE:
                        SERVICE_CACHE.cleanCache(ServiceMetaInfo.getServiceKey(nodeKey));
                        break;
                    case PUT:
                    default:
                        break;
                }
            }
        });

        // watch it successfully, add it to set
        WATCHED_KEY_SET.add(nodeKey);

    }

    @Override
    public void refresh() {
        // set the schedule task
        CronUtil.schedule("*/10 * * * * *", (Task) () -> {
            for (String registryKey : NODE_KEY_SET) {
                // search service meta information from etcd
                ByteSequence key = ByteSequence.from(registryKey, StandardCharsets.UTF_8);

                try {
                    List<KeyValue> keyValueList = kvClient.get(key).get().getKvs();

                    if (CollectionUtil.isEmpty(keyValueList)) continue;

                    ByteSequence value = keyValueList.get(0).getValue();

                    kvClient.put(key, value, getLeaseTime(30));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }


            }
        });

        // start the schedule task
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    @Override
    public void increaseUsage(ServiceMetaInfo serviceMetaInfo) {
        // increase usage number
        serviceMetaInfo.setUsedNumber(serviceMetaInfo.getUsedNumber() + 1);
        store(serviceMetaInfo, 30);
    }

    @Override
    public void decreaseUsage(ServiceMetaInfo serviceMetaInfo) {
        // increase usage number
        serviceMetaInfo.setUsedNumber(serviceMetaInfo.getUsedNumber() - 1);
        store(serviceMetaInfo, 30);
    }


    /**
     * @param timeout (seconds)
     */
    private PutOption getLeaseTime(long timeout) throws Exception {
        // create a lease time
        long leaseId = client.getLeaseClient().grant(timeout).get().getID();
        // transform it to put-option
        return PutOption.builder().withLeaseId(leaseId).build();
    }


    /**
     * transform a node key to tool key
     * and the tool key will help to refresh the service cache when a new service is registered
     */
    private String getToolKey(String nodeKey) {
        int i = nodeKey.length() - 1;
        int count = 0;
        while (count < 2) {
            if (nodeKey.charAt(i--) == '/') count++;
        }

        return nodeKey.substring(0, i + 2) + "tool";
    }


    /**
     * store a service and set its lease time
     * @param timeout seconds
     */
    private void store(ServiceMetaInfo serviceMetaInfo, long timeout) {
        // prepare k-v
        String registryKey = ETCD_ROOT_PATH + serviceMetaInfo.getNodeKey();
        ByteSequence key = ByteSequence.from(registryKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        // put it to registry center
        try {
            kvClient.put(key, value, getLeaseTime(timeout)).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
