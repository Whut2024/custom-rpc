package com.whut.rpc.core.proxy.template;

import cn.hutool.core.collection.CollectionUtil;
import com.whut.rpc.core.config.RegistryConfig;
import com.whut.rpc.core.config.RpcApplication;
import com.whut.rpc.core.config.RpcConfig;
import com.whut.rpc.core.loadbalancer.LoadBalancer;
import com.whut.rpc.core.loadbalancer.LoadBalancerFactory;
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
        RpcConfig rpcConfig = RpcApplication.getConfig();

        String serviceName = method.getDeclaringClass().getName();

        final String methodName = method.getName();
        final RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(methodName)
                .argsClassType(method.getParameterTypes())
                .args(args)
                .build();

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
            LoadBalancer loadBalancer = LoadBalancerFactory.get(rpcConfig.getLoadBalancer());
            ServiceMetaInfo serviceMetaInfo = loadBalancer.select(requestParamMap, serviceMetaInfoList);

            return getResponse(serviceMetaInfo, rpcRequest).getResponseData();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }


    public abstract RpcResponse getResponse(ServiceMetaInfo serviceMetaInfo, RpcRequest rpcRequest) throws IOException;
}
