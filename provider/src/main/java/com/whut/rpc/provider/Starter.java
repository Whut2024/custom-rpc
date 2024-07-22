package com.whut.rpc.provider;

import com.whut.rpc.esay.server.impl.VertxHttpServer;

/**
 * service starter class
 */
public class Starter {

    public static void main(String[] args) {
        VertxHttpServer server = new VertxHttpServer();

        server.start(8080);
    }
}
