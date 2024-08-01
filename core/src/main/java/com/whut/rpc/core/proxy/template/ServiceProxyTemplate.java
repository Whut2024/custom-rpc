package com.whut.rpc.core.proxy.template;

import cn.hutool.core.collection.CollectionUtil;
import com.whut.rpc.core.config.RegistryConfig;
import com.whut.rpc.core.config.RpcApplication;
import com.whut.rpc.core.config.RpcConfig;
import com.whut.rpc.core.fault.retry.RetryStrategy;
import com.whut.rpc.core.fault.retry.RetryStrategyFactory;
import com.whut.rpc.core.loadbalancer.LoadBalancer;
import com.whut.rpc.core.loadbalancer.LoadBalancerFactory;
import com.whut.rpc.core.loadbalancer.impl.LeastUsageLoadBalancer;
import com.whut.rpc.core.model.RpcRequest;
import com.whut.rpc.core.model.RpcResponse;
import com.whut.rpc.core.model.ServiceMetaInfo;
import com.whut.rpc.core.registry.BasicRegistry;
import com.whut.rpc.core.registry.RegistryFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * the process logic of service proxy
 *
 * @author whut2024
 * @since 2024-07-23
 */
public abstract class ServiceProxyTemplate implements InvocationHandler {


    @Override
    public final Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final RpcConfig rpcConfig = RpcApplication.getConfig();

        final String serviceName = method.getDeclaringClass().getName();

        final String methodName = method.getName();
        final RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(methodName)
                .argsClassType(method.getParameterTypes())
                .args(args)
                .build();

        LoadBalancer loadBalancer = null;
        ServiceMetaInfo serviceMetaInfo = null;
        try {
            // discovery remote matched service list
            final ServiceMetaInfo tempInfo = new ServiceMetaInfo();
            tempInfo.setName(serviceName);
            final String serviceKey = tempInfo.getServiceKey();

            final RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            final BasicRegistry registryClient = RegistryFactory.get(registryConfig.getType());
            final List<ServiceMetaInfo> serviceMetaInfoList = registryClient.discovery(serviceKey);

            if (CollectionUtil.isEmpty(serviceMetaInfoList)) throw new RuntimeException("no such specified service");

            // load balance
            final Map<String, Object> requestParamMap = new HashMap<>();
            requestParamMap.put("methodName", methodName);
            loadBalancer = LoadBalancerFactory.get(rpcConfig.getLoadBalancer());
            serviceMetaInfo = loadBalancer.select(requestParamMap, serviceMetaInfoList);

            // get retry strategy
            final RetryStrategy retryStrategy = RetryStrategyFactory.get(rpcConfig.getRetryStrategy());

            try {
                final ServiceMetaInfo finalServiceMetaInfo = serviceMetaInfo;
                final RpcResponse rpcResponse = retryStrategy.doRetry(() -> getResponse(finalServiceMetaInfo, rpcRequest));
                return rpcResponse.getResponseData();
            } finally {
                if (loadBalancer instanceof LeastUsageLoadBalancer) ((LeastUsageLoadBalancer) loadBalancer).over(serviceMetaInfo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }


    public abstract RpcResponse getResponse(ServiceMetaInfo serviceMetaInfo, RpcRequest rpcRequest) throws IOException;
}
