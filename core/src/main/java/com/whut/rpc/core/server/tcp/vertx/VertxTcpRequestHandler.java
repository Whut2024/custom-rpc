package com.whut.rpc.core.server.tcp.vertx;

import com.whut.rpc.core.model.RpcRequest;
import com.whut.rpc.core.model.RpcResponse;
import com.whut.rpc.core.protocol.ProtocolMessage;
import com.whut.rpc.core.protocol.coder.ProtocolMessageCoder;
import com.whut.rpc.core.protocol.coder.VertxCoder;
import com.whut.rpc.core.protocol.enums.ProtocolMessageTypeEnum;
import com.whut.rpc.core.registry.LocalRegistry;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * tcp server handler
 *
 * @author whut2024
 * @since 2024-07-29
 */
@Slf4j
public class VertxTcpRequestHandler implements Handler<NetSocket> {


    private final ProtocolMessageCoder<Buffer> coder = VertxCoder.getInstance();


    @Override
    public void handle(NetSocket socket) {

        socket.handler(buffer -> {
            ProtocolMessage<?> protocolMessage = coder.decoder(buffer);

            RpcResponse rpcResponse = new RpcResponse();

            try {
                // prepare rpc-request object
                RpcRequest rpcRequest = (RpcRequest) protocolMessage.getBody();

                // invoke specified method
                String serviceName = rpcRequest.getServiceName();
                String methodName = rpcRequest.getMethodName();
                Class<?>[] argsClassType = rpcRequest.getArgsClassType();
                Object[] args = rpcRequest.getArgs();

                Class<?> service = LocalRegistry.getService(serviceName);
                Method method = service.getMethod(methodName, argsClassType);
                // todo    instantiate class by constructor
                Object result = method.invoke(service.getConstructor().newInstance(), args);

                // inject message
                rpcResponse.setResponseData(result);
                rpcResponse.setResponseDataClassType(result.getClass());
            } catch (Exception e) {
                // note error message
                rpcResponse.setException(e);
                rpcResponse.setResponseMessage(e.getMessage());

                log.warn("handler failed, cause is {}", e.getMessage());
            }

            // response
            ProtocolMessage.Header header = protocolMessage.getHeader();
            header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
            ProtocolMessage<RpcResponse> rpcResponseProtocolMessage = new ProtocolMessage<>(rpcResponse, header);

            Buffer responseBuffer;
            try {
                responseBuffer = coder.encoder(rpcResponseProtocolMessage);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("encoding failed");
            }

            socket.write(responseBuffer);
        });


    }
}
