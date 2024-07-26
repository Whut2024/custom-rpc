package com.whut.rpc.core.proxy;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.whut.rpc.core.config.RegistryConfig;
import com.whut.rpc.core.config.RpcApplication;
import com.whut.rpc.core.config.RpcConfig;
import com.whut.rpc.core.model.RpcRequest;
import com.whut.rpc.core.model.RpcResponse;
import com.whut.rpc.core.model.ServiceMetaInfo;
import com.whut.rpc.core.registry.BasicRegistry;
import com.whut.rpc.core.registry.RegistryFactory;
import com.whut.rpc.core.serializer.BasicSerializer;
import com.whut.rpc.core.serializer.SerializerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * the process logic of service proxy
 *
 * @author whut2024
 * @since 2024-07-23
 */
public class ServiceProxy implements InvocationHandler {

    /**
     * specified serializer
     */
    private final static BasicSerializer serializer = SerializerFactory.getSerializer(RpcApplication.getConfig().getSerializer());


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String serviceName = method.getDeclaringClass().getName();

        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .argsClassType(method.getParameterTypes())
                .args(args)
                .build();

        byte[] rpcRequestByteArray = serializer.serialize(rpcRequest);

        try {
            RpcConfig rpcConfig = RpcApplication.getConfig();

            // discovery remote matched service list
            ServiceMetaInfo tempInfo = new ServiceMetaInfo();
            tempInfo.setName(serviceName);
            String serviceKey = tempInfo.getServiceKey();

            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            BasicRegistry registryClient = RegistryFactory.get(registryConfig.getType());
            List<ServiceMetaInfo> serviceMetaInfoList = registryClient.discovery(serviceKey);

            if (CollectionUtil.isEmpty(serviceMetaInfoList)) throw new RuntimeException("no such specified service");

            // todo we just use the first service this time
            ServiceMetaInfo serviceMetaInfo = serviceMetaInfoList.get(0);

            // send the post and get the response
            HttpResponse response = HttpRequest.post(serviceMetaInfo.getFullServiceAddress()).body(rpcRequestByteArray).execute();

            // resolve response
            byte[] rpcResponseByteArray = response.bodyBytes();
            RpcResponse rpcResponse = serializer.deserialize(rpcResponseByteArray, RpcResponse.class);

            return rpcResponse.getResponseData();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }
}
