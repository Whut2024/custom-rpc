package com.whut.rpc.core.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.whut.rpc.core.config.RpcApplication;
import com.whut.rpc.core.config.RpcConfig;
import com.whut.rpc.core.model.RpcRequest;
import com.whut.rpc.core.model.RpcResponse;
import com.whut.rpc.core.serializer.BasicSerializer;
import com.whut.rpc.core.serializer.SerializerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

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

        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .argsClassType(method.getParameterTypes())
                .args(args)
                .build();

        byte[] rpcRequestByteArray = serializer.serialize(rpcRequest);

        try {
            RpcConfig rpcConfig = RpcApplication.getConfig();
            HttpResponse response = HttpRequest.post(String.format("http://%s:%s", rpcConfig.getHost(), rpcConfig.getPort())).body(rpcRequestByteArray).execute();

            byte[] rpcResponseByteArray = response.bodyBytes();
            RpcResponse rpcResponse = serializer.deserialize(rpcResponseByteArray, RpcResponse.class);

            return rpcResponse.getResponseData();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }
}
