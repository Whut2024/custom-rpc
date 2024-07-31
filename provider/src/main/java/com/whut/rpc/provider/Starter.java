package com.whut.rpc.provider;

import com.whut.rpc.common.servcie.UserService;
import com.whut.rpc.core.config.RpcApplication;
import com.whut.rpc.core.config.RpcConfig;
import com.whut.rpc.core.model.ServiceMetaInfo;
import com.whut.rpc.core.registry.BasicRegistry;
import com.whut.rpc.core.registry.LocalRegistry;
import com.whut.rpc.core.registry.RegistryFactory;
import com.whut.rpc.core.server.http.vertx.VertxServer;
import com.whut.rpc.core.server.tcp.vertx.VertxTcpServer;
import com.whut.rpc.provider.service.impl.UserServiceImpl;

import static com.whut.rpc.core.registry.RegistryKeys.*;

/**
 * service starter class
 */
public class Starter {

    public static void main(String[] args) throws Exception {

        // get config object
        RpcConfig rpcConfig = RpcApplication.getConfig();
        // register a service implementation
        LocalRegistry.addService(UserService.class.getName(), UserServiceImpl.class);

        // get registry center object
        BasicRegistry registry = RegistryFactory.get(ETCD);

        // registry service
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setName(UserService.class.getName());
        serviceMetaInfo.setHost(rpcConfig.getHost());
        serviceMetaInfo.setPort(rpcConfig.getPort());
        serviceMetaInfo.setUsedNumber(0);
        registry.register(serviceMetaInfo);


        // start web server
        VertxTcpServer server = new VertxTcpServer();

        server.start(rpcConfig.getPort());
    }
}
