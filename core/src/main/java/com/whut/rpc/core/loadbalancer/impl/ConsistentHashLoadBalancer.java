package com.whut.rpc.core.loadbalancer.impl;

import com.whut.rpc.core.loadbalancer.LoadBalancer;
import com.whut.rpc.core.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * consistent hash
 * @author whut2024
 * @since 2024-07-31
 */
public class ConsistentHashLoadBalancer implements LoadBalancer {


    private final static int VIRTUAL_NODE_NUM = 100;


    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParamMap, List<ServiceMetaInfo> availableServiceList) {
        final int size = availableServiceList.size();

        if (size == 0) return null;
        if (size == 1) return availableServiceList.get(0);

        // generate consistent hash round
        final TreeMap<Integer, ServiceMetaInfo> virtualNodeTreeMap = new TreeMap<>();
        for (ServiceMetaInfo serviceMetaInfo : availableServiceList) {
            for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
                virtualNodeTreeMap.put(hash(serviceMetaInfo.getFullServiceAddress() + "#" + i), serviceMetaInfo);
            }
        }

        // get result
        final Integer resultHash = hash(requestParamMap);

        Map.Entry<Integer, ServiceMetaInfo> entry = virtualNodeTreeMap.ceilingEntry(resultHash);
        if (entry == null) entry = virtualNodeTreeMap.firstEntry();

        return entry.getValue();


    }


    /**
     * self defined hash algorithm
     */
    private Integer hash(Object key) {
        return key.hashCode();
    }
}
