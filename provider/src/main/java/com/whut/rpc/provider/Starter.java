package com.whut.rpc.provider;

import com.whut.rpc.common.servcie.UserService;
import com.whut.rpc.esay.server.impl.vertx.VertxHttpServer;
import com.whut.rpc.esay.registry.LocalRegistry;
import com.whut.rpc.provider.service.impl.UserServiceImpl;

/**
 * service starter class
 */
public class Starter {

    public static void main(String[] args) {
        // register a service implementation
        LocalRegistry.addService(UserService.class.getName(), UserServiceImpl.class);


        // start web server
        VertxHttpServer server = new VertxHttpServer();

        server.start(8080);
    }
}
