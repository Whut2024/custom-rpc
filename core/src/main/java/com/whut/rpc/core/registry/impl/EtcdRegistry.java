package com.whut.rpc.core.registry.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.whut.rpc.core.config.RegistryConfig;
import com.whut.rpc.core.model.ServiceMetaInfo;
import com.whut.rpc.core.registry.BasicRegistry;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
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


    private final static String ETCD_ROOT_PATH = "/rpc/";


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

    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        // create a lease time
        long leaseId = client.getLeaseClient().grant(3000).get().getID();

        // prepare k-v
        String registryKey = ETCD_ROOT_PATH + serviceMetaInfo.getNodeKey();
        ByteSequence key = ByteSequence.from(registryKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();

        // put it to registry center
        kvClient.put(key, value, putOption).get();

    }

    @Override
    public void unregister(ServiceMetaInfo serviceMetaInfo) {
        String registryKey = ETCD_ROOT_PATH + serviceMetaInfo.getNodeKey();
        ByteSequence key = ByteSequence.from(registryKey, StandardCharsets.UTF_8);
        kvClient.delete(key);
    }

    @Override
    public List<ServiceMetaInfo> discovery(String serviceKey) {
        // prefix key
        String prefixKey = ETCD_ROOT_PATH + serviceKey + "/";

        // get meta data
        GetOption getOption = GetOption.builder().isPrefix(true).build();
        try {
            List<KeyValue> keyValueList = kvClient.get(ByteSequence.from(prefixKey, StandardCharsets.UTF_8), getOption).get().getKvs();

            return keyValueList.stream().map(keyValue -> {
                String value = keyValue.getValue().toString(StandardCharsets.UTF_8);

                return JSONUtil.toBean(value, ServiceMetaInfo.class);
            }).collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("discovery service failed");
        }


    }

    @Override
    public void destroy() {
        log.warn("etcd registry client is going to close");

        if (kvClient != null) kvClient.close();

        if (client != null) client.close();

    }
}
