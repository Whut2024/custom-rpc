package com.whut.rpc.esay.server.impl;

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
        server.requestHandler(event -> {
            System.out.printf("request method is: %s, URI is: %s\n", event.method().toString(), event.uri());

            event.response()
                    .putHeader("content-type", "application/json")
                    .end("----------starter vertx web server success----------");
        });

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
