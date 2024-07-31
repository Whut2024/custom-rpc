package com.whut.rpc.core.loadbalancer.impl;

import com.whut.rpc.core.config.RpcApplication;
import com.whut.rpc.core.loadbalancer.LoadBalancer;
import com.whut.rpc.core.model.ServiceMetaInfo;
import com.whut.rpc.core.registry.BasicRegistry;
import com.whut.rpc.core.registry.RegistryFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * the least usage load balancer
 * @author whut2024
 * @since 2024-07-31
 */
public class LeastUsageLoadBalancer implements LoadBalancer {


    /**
     * whether we can use asynchronous operation
     */
    //private final ExecutorService executor = Executors.newCachedThreadPool();


    private final BasicRegistry registry = RegistryFactory.get(RpcApplication.getConfig().getRegistryConfig().getType());


    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParamMap, List<ServiceMetaInfo> availableServiceList) {
        if (availableServiceList.isEmpty()) return null;

        List<ServiceMetaInfo> serviceMetaInfoList = availableServiceList.stream().sorted().collect(Collectors.toList());
        ServiceMetaInfo serviceMetaInfo = serviceMetaInfoList.get(0);

        registry.increaseUsage(serviceMetaInfo);
        return serviceMetaInfo;
    }


    /**
     * the connection number be decreased
     */
    public void over(ServiceMetaInfo serviceMetaInfo) {
        registry.decreaseUsage(serviceMetaInfo);
    }


}
