package com.whut.rpc.core.fault.tolerant.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.whut.rpc.core.config.RpcApplication;
import com.whut.rpc.core.config.RpcConfig;
import com.whut.rpc.core.fault.tolerant.TolerantStrategy;
import com.whut.rpc.core.loadbalancer.LoadBalancer;
import com.whut.rpc.core.loadbalancer.LoadBalancerFactory;
import com.whut.rpc.core.model.RpcRequest;
import com.whut.rpc.core.model.RpcResponse;
import com.whut.rpc.core.model.ServiceMetaInfo;
import com.whut.rpc.core.registry.BasicRegistry;
import com.whut.rpc.core.registry.RegistryFactory;
import com.whut.rpc.core.server.tcp.vertx.VertxTcpClient;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.whut.rpc.core.fault.tolerant.TolerantStrategyKeys.*;

/**
 * try to use other usable node
 *
 * @author whut2024
 * @since 2024-08-02
 */
public class FailOverTolerantStrategy implements TolerantStrategy {


    private final Map<String, LocalDateTime> faultyNodeMap = new ConcurrentHashMap<>();


    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) throws Exception {
        final ServiceMetaInfo serviceMetaInfo = (ServiceMetaInfo) context.get(SERVICE_META_INFO);
        if (serviceMetaInfo == null) throw new NullPointerException("service-meta-info is null");

        // store faulty service node
        faultyNodeMap.put(serviceMetaInfo.getNodeKey(), LocalDateTime.now().plusSeconds(30L));

        final RpcConfig rpcConfig = RpcApplication.getConfig();
        final BasicRegistry registry = RegistryFactory.get(rpcConfig.getRegistryConfig().getType());
        final LoadBalancer loadBalancer = LoadBalancerFactory.get(rpcConfig.getLoadBalancer());

        // get all related service nodes
        List<ServiceMetaInfo> serviceMetaInfoList = registry.discovery(serviceMetaInfo.getServiceKey());
        if (CollectionUtil.isEmpty(serviceMetaInfoList)) throw new RuntimeException("service-node-list is empty");

        // filter faulty service nodes
        List<ServiceMetaInfo> usableNodeList = serviceMetaInfoList.stream().filter(node -> {
            LocalDateTime expireTime = faultyNodeMap.get(node.getNodeKey());
            return expireTime == null || expireTime.isBefore(LocalDateTime.now());
        }).collect(Collectors.toList());

        // load balance it
        ServiceMetaInfo usableNode = loadBalancer.select(context, usableNodeList);

        // send request
        RpcRequest rpcRequest = (RpcRequest) context.get(RPC_REQUEST);
        return VertxTcpClient.doRequest(usableNode, rpcRequest);
    }
}
