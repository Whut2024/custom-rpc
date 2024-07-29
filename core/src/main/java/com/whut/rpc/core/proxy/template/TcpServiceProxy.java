package com.whut.rpc.core.proxy.template;

import com.whut.rpc.core.config.RpcApplication;
import com.whut.rpc.core.config.RpcConfig;
import com.whut.rpc.core.constant.ProtocolConstant;
import com.whut.rpc.core.model.RpcRequest;
import com.whut.rpc.core.model.RpcResponse;
import com.whut.rpc.core.model.ServiceMetaInfo;
import com.whut.rpc.core.protocol.ProtocolMessage;
import com.whut.rpc.core.protocol.coder.ProtocolMessageCoder;
import com.whut.rpc.core.protocol.coder.VertxCoder;
import com.whut.rpc.core.protocol.enums.ProtocolMessageSerializerEnum;
import com.whut.rpc.core.protocol.enums.ProtocolMessageStatusEnum;
import com.whut.rpc.core.protocol.enums.ProtocolMessageTypeEnum;
import com.whut.rpc.core.serializer.BasicSerializer;
import com.whut.rpc.core.serializer.SerializerFactory;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.whut.rpc.core.constant.ProtocolConstant.*;

/**
 * use tcp to connect directly
 *
 * @author whut2024
 * @since 2024-07-29
 */
@Slf4j
public class TcpServiceProxy extends ServiceProxyTemplate {


    private final ProtocolMessageCoder<Buffer> coder = VertxCoder.getInstance();


    @Override
    public RpcResponse getResponse(ServiceMetaInfo serviceMetaInfo, RpcRequest rpcRequest) throws IOException {

        // build connect
        NetClient client = Vertx.vertx().createNetClient();
        RpcConfig rpcConfig = RpcApplication.getConfig();

        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
        client.connect(rpcConfig.getPort(), rpcConfig.getHost(), connect -> {
            // get socket
            if (connect.succeeded()) {
                NetSocket socket = connect.result();

                // resolve message
                ProtocolMessage.Header header = new ProtocolMessage.Header();

                header.setMagic(PROTOCOL_MAGIC);
                header.setVersion(PROTOCOL_VERSION);
                header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                header.setStatus((byte) ProtocolMessageStatusEnum.OK.getValue());

                ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getByValue(rpcConfig.getSerializer());
                if (serializerEnum == null) throw new RuntimeException("no such specified serializer");
                header.setSerializer((byte) serializerEnum.getKey());

                ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>(rpcRequest, header);
                Buffer buffer = coder.encoder(protocolMessage);

                socket.write(buffer);

                socket.handler(responseBuffer -> {
                    try {
                        ProtocolMessage<?> responseProtocolMessage = coder.decoder(responseBuffer);

                        responseFuture.complete((RpcResponse) responseProtocolMessage.getBody());
                    } catch (Exception e) {
                        log.warn("decoding failed");
                        throw new RuntimeException(e);
                    }
                });
            } else {
                log.error("connecting failed");
            }
        });

        try {
            RpcResponse rpcResponse = responseFuture.get(3000, TimeUnit.SECONDS);

            client.close();

            return rpcResponse;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }


    }
}
