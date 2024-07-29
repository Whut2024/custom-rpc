package com.whut.rpc.core.proxy.template;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.whut.rpc.core.config.RpcApplication;
import com.whut.rpc.core.model.RpcRequest;
import com.whut.rpc.core.model.RpcResponse;
import com.whut.rpc.core.model.ServiceMetaInfo;
import com.whut.rpc.core.serializer.BasicSerializer;
import com.whut.rpc.core.serializer.SerializerFactory;

import java.io.IOException;

/**
 * use http to connect
 *
 * @author whut2024
 * @since 2024-07-29
 */
public class HttpServiceProxy extends ServiceProxyTemplate {


    /**
     * the specified serializer
     */
    private final BasicSerializer serializer = SerializerFactory.getSerializer(RpcApplication.getConfig().getSerializer());


    @Override
    public RpcResponse getResponse(ServiceMetaInfo serviceMetaInfo, RpcRequest rpcRequest) throws IOException {
        // send the post and get the response
        HttpResponse response = HttpRequest.post(serviceMetaInfo.getFullServiceAddress()).body(serializer.serialize(rpcRequest)).execute();

        // resolve response
        byte[] rpcResponseByteArray = response.bodyBytes();
        return serializer.deserialize(rpcResponseByteArray, RpcResponse.class);
    }
}
