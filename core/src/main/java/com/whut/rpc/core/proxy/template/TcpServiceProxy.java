package com.whut.rpc.core.proxy.template;

import com.whut.rpc.core.model.RpcRequest;
import com.whut.rpc.core.model.RpcResponse;
import com.whut.rpc.core.model.ServiceMetaInfo;
import com.whut.rpc.core.server.tcp.vertx.VertxTcpClient;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * use tcp to connect directly
 *
 * @author whut2024
 * @since 2024-07-29
 */
@Slf4j
public class TcpServiceProxy extends ServiceProxyTemplate {




    @Override
    public RpcResponse getResponse(ServiceMetaInfo serviceMetaInfo, RpcRequest rpcRequest) throws IOException {
        return VertxTcpClient.doRequest(serviceMetaInfo, rpcRequest);
    }
}
