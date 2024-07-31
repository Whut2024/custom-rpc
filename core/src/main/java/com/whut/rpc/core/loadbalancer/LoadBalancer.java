package com.whut.rpc.core.loadbalancer;

import com.whut.rpc.core.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

/**
 * the interface for Load Balance
 *
 * @author whut2024
 * @since 2024-07-31
 */
public interface LoadBalancer {


    /**
     * according to requestParams and available service, select a 'load balance' service
     */
    ServiceMetaInfo select(Map<String, Object> requestParamMap, List<ServiceMetaInfo> availableServiceList);
}
