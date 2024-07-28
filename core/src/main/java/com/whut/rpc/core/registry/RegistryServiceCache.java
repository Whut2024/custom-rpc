package com.whut.rpc.core.registry;

import com.whut.rpc.core.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * cache the information of registered service
 *
 * @author whut2024
 * @since 2024-07-28
 */
public class RegistryServiceCache {

    private final Map<String, List<ServiceMetaInfo>> serviceMetaInfoMap = new ConcurrentHashMap<>();


    public void writeCache(String serviceKey, List<ServiceMetaInfo> serviceMetaInfoList) {
        this.serviceMetaInfoMap.put(serviceKey, serviceMetaInfoList);
    }

    public List<ServiceMetaInfo> readCache(String serviceKey) {
        return this.serviceMetaInfoMap.get(serviceKey);
    }


    public void cleanCache(String serviceKey) {
        this.serviceMetaInfoMap.remove(serviceKey);
    }
}
