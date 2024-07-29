package com.whut.rpc.core.server.tcp.vertx;

import com.whut.rpc.core.server.BasicServer;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;
import lombok.extern.slf4j.Slf4j;


/**
 * start a tcp vertx server
 *
 * @author whut2024
 * @since 2024-07-29
 */
@Slf4j
public class VertxTcpServer implements BasicServer {

    @Override
    public void start(int port) {
        Vertx vertx = Vertx.vertx();

        NetServer server = vertx.createNetServer();

        // register request handler
        server.connectHandler(new VertxTcpRequestHandler());

        // bind a port and log the starting result
        server.listen(port, result -> {
            if (result.succeeded()) {
                log.info("tcp web server started successfully");
            } else {
                log.error("tcp web server starting failed, the cause is {}", result.cause().toString());
            }
        });
    }
}
