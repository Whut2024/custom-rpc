package com.whut.rpc.esay.server.impl.vertx;

import com.whut.rpc.esay.server.BasicHttpServer;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;

/**
 * a web server depends on vertx
 */
public class VertxHttpServer implements BasicHttpServer {
    @Override
    public void start(int port) {
        // initialization
        HttpServer server = Vertx.vertx().createHttpServer();

        // add request handler
        server.requestHandler(new RequestHandler());

        // bind a port
        server.listen(port, result -> {
            if (result.succeeded()) {
                System.out.printf("server is listening %d port\n", port);
            } else {
                System.out.printf("server started fail，the result is: %s\n", result.cause());
            }

        });
    }
}
