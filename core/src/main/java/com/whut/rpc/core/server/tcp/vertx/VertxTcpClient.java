package com.whut.rpc.core.server.tcp.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * @author whut2024
 * @since 2024-07-29
 */
@Slf4j
public class VertxTcpClient {

    public void start() {
        Vertx vertx = Vertx.vertx();

        vertx.createNetClient().connect(8081, "127.0.0.1", connect -> {
            if (connect.succeeded()) {
                log.info("tcp connected successfully");

                // send message
                NetSocket socket = connect.result();

                socket.write(Buffer.buffer("LFF".getBytes(StandardCharsets.UTF_8)));


                // receive response
                socket.handler(buffer -> {
                    log.info("the response is {}", new String(buffer.getBytes(), StandardCharsets.UTF_8));
                });
            } else {
                log.error("connecting failed");
            }
        });
    }
}
