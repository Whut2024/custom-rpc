package com.whut.rpc.core.loadbalancer.impl;

import com.whut.rpc.core.loadbalancer.LoadBalancer;
import com.whut.rpc.core.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * round-robin balance loader
 *
 * @author whut2024
 * @since 2024-07-31
 */
public class RoundRobinLoadBalancer implements LoadBalancer {


    /**
     * note every service's index
     */
    private final Map<String, AtomicInteger> serviceIndexMap = new ConcurrentHashMap<>();


    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParamMap, List<ServiceMetaInfo> availableServiceList) {
        final int size = availableServiceList.size();

        if (size == 0) return null;

        ServiceMetaInfo firstService = availableServiceList.get(0);
        if (size == 1) return firstService;

        final String serviceKey = firstService.getServiceKey();
        final AtomicInteger index = serviceIndexMap.computeIfAbsent(serviceKey, k -> new AtomicInteger(0));

        return availableServiceList.get(index.getAndIncrement() % size);
    }
}
