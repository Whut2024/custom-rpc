package com.whut.rpc.core.server.tcp.vertx;

import cn.hutool.core.util.IdUtil;
import com.whut.rpc.core.config.RpcApplication;
import com.whut.rpc.core.config.RpcConfig;
import com.whut.rpc.core.model.RpcRequest;
import com.whut.rpc.core.model.RpcResponse;
import com.whut.rpc.core.model.ServiceMetaInfo;
import com.whut.rpc.core.protocol.ProtocolMessage;
import com.whut.rpc.core.protocol.coder.ProtocolMessageCoder;
import com.whut.rpc.core.protocol.coder.VertxCoder;
import com.whut.rpc.core.protocol.enums.ProtocolMessageSerializerEnum;
import com.whut.rpc.core.protocol.enums.ProtocolMessageStatusEnum;
import com.whut.rpc.core.protocol.enums.ProtocolMessageTypeEnum;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.whut.rpc.core.constant.ProtocolConstant.PROTOCOL_MAGIC;
import static com.whut.rpc.core.constant.ProtocolConstant.PROTOCOL_VERSION;

/**
 * use vertx to achieve tcp connect
 * @author whut2024
 * @since 2024-07-29
 */
@Slf4j
public class VertxTcpClient {


    private final static ProtocolMessageCoder<Buffer> coder = VertxCoder.getInstance();


    public static RpcResponse doRequest(ServiceMetaInfo serviceMetaInfo, RpcRequest rpcRequest) {
        // build connect
        NetClient client = Vertx.vertx().createNetClient();
        RpcConfig rpcConfig = RpcApplication.getConfig();

        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
        client.connect(serviceMetaInfo.getPort(), serviceMetaInfo.getHost(), connect -> {
            // get socket
            if (connect.succeeded()) {
                NetSocket socket = connect.result();

                // resolve message
                ProtocolMessage.Header header = new ProtocolMessage.Header();

                // prepare header
                header.setMagic(PROTOCOL_MAGIC);
                header.setVersion(PROTOCOL_VERSION);
                header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                header.setStatus((byte) ProtocolMessageStatusEnum.OK.getValue());
                header.setRequestId(IdUtil.getSnowflakeNextId());

                ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getByValue(rpcConfig.getSerializer());
                if (serializerEnum == null) throw new RuntimeException("no such specified serializer");
                header.setSerializer((byte) serializerEnum.getKey());

                ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>(rpcRequest, header);
                Buffer buffer = coder.encoder(protocolMessage);

                socket.write(buffer);

                socket.handler(new TcpBufferHandlerWrapper(
                        responseBuffer -> {
                            try {
                                ProtocolMessage<?> responseProtocolMessage = coder.decoder(responseBuffer);

                                responseFuture.complete((RpcResponse) responseProtocolMessage.getBody());
                            } catch (Exception e) {
                                log.warn("decoding failed");
                                throw new RuntimeException(e);
                            }
                        }
                ));
            } else {
                log.error("connecting failed");
            }
        });

        try {
            RpcResponse rpcResponse = responseFuture.get(10, TimeUnit.SECONDS);

            client.close();

            return rpcResponse;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

}
