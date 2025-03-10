package com.whut.rpc.core.server.http.vertx;

import com.whut.rpc.core.server.BasicServer;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import lombok.extern.slf4j.Slf4j;

/**
 * a web server depends on vertx
 */
@Slf4j
public class VertxServer implements BasicServer {
    @Override
    public void start(int port) {
        // initialization
        HttpServer server = Vertx.vertx().createHttpServer();

        // add request handler
        server.requestHandler(new RequestHandler());

        // bind a port
        server.listen(port, result -> {
            if (result.succeeded()) {
                log.info("server is listening {} port", port);
            } else {
                log.info("server started fail，the result is: {}", result.cause().toString());
            }

        });
    }
}
