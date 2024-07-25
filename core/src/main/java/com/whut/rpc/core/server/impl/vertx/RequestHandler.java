package com.whut.rpc.core.server.impl.vertx;

import com.whut.rpc.core.config.RpcApplication;
import com.whut.rpc.core.model.RpcRequest;
import com.whut.rpc.core.model.RpcResponse;
import com.whut.rpc.core.registry.LocalRegistry;
import com.whut.rpc.core.serializer.BasicSerializer;
import com.whut.rpc.core.serializer.SerializerFactory;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author whut2024
 * @since 2024-07-23
 *
 * handler request from consumer
 */
public class RequestHandler implements Handler<HttpServerRequest> {


    /**
     * specified serializer
     */
    private final static BasicSerializer serializer = SerializerFactory.getSerializer(RpcApplication.getConfig().getSerializer());


    @Override
    public void handle(HttpServerRequest request) {

        request.bodyHandler(body -> {
            RpcRequest rpcRequest = null;
            RpcResponse rpcResponse = new RpcResponse();

            // prepare rpc-request object
            byte[] byteArray = body.getBytes();

            try {
                rpcRequest = serializer.deserialize(byteArray, RpcRequest.class);

                if (rpcRequest == null) {
                    rpcResponse.setResponseMessage("rpc-request is null");
                    doResponse(request, rpcResponse);
                }
            } catch (IOException e) {
                rpcResponse.setException(e);
                rpcResponse.setResponseMessage(e.getMessage());
                doResponse(request, rpcResponse);
            }

            // invoke specified method
            String serviceName = rpcRequest.getServiceName();
            String methodName = rpcRequest.getMethodName();
            Class<?>[] argsClassType = rpcRequest.getArgsClassType();
            Object[] args = rpcRequest.getArgs();

            Class<?> service = LocalRegistry.getService(serviceName);
            Object result = null;
            try {
                Method method = service.getMethod(methodName, argsClassType);
                // todo    instantiate class by constructor
                result = method.invoke(service.getConstructor().newInstance(), args);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                     InstantiationException e) {
                rpcResponse.setException(e);
                rpcResponse.setResponseMessage(e.getMessage());
                doResponse(request, rpcResponse);

                throw new RuntimeException(e);
            }

            // inject message
            rpcResponse.setResponseData(result);
            rpcResponse.setResponseDataClassType(result.getClass());

            // response
            doResponse(request, rpcResponse);
        });
    }


    private void doResponse(HttpServerRequest request, RpcResponse rpcResponse) {
        HttpServerResponse response = request.response();

        response.putHeader("content-type", "text/plain");

        try {
            response.end(Buffer.buffer(serializer.serialize(rpcResponse)));
        } catch (IOException e) {
            e.printStackTrace();
            response.end(Buffer.buffer());
        }
    }
}
