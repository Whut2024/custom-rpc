package com.whut.rpc.core.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.whut.rpc.core.model.RpcRequest;
import com.whut.rpc.core.model.RpcResponse;
import com.whut.rpc.core.serializer.BasicSerializer;
import com.whut.rpc.core.serializer.impl.JdkSerializer;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author whut2024
 * @since 2024-07-23
 *
 *
 * the process logic of service proxy
 */
public class ServiceProxy implements InvocationHandler {


    private final static BasicSerializer serializer = new JdkSerializer();


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
            HttpResponse response = HttpRequest.post("http://localhost:8080").body(rpcRequestByteArray).execute();

            byte[] rpcResponseByteArray = response.bodyBytes();
            RpcResponse rpcResponse = serializer.deserialize(rpcResponseByteArray);

            return rpcResponse.getResponseData();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }
}
