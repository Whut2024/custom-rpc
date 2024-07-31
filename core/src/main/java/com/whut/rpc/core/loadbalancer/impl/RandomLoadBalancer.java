package com.whut.rpc.core.loadbalancer.impl;

import com.whut.rpc.core.loadbalancer.LoadBalancer;
import com.whut.rpc.core.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * select a service randomly
 *
 * @author whut2024
 * @since 2024-07-31
 */
public class RandomLoadBalancer implements LoadBalancer {


    private final Random random = new Random();


    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParamMap, List<ServiceMetaInfo> availableServiceList) {

        final int size = availableServiceList.size();

        if (size == 0) return null;
        if (size == 1) return availableServiceList.get(0);

        return availableServiceList.get(random.nextInt(size));
    }
}
