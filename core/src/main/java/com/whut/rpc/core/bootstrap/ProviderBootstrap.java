package com.whut.rpc.core.bootstrap;

import com.whut.rpc.core.config.RegistryConfig;
import com.whut.rpc.core.config.RpcApplication;
import com.whut.rpc.core.config.RpcConfig;
import com.whut.rpc.core.model.ServiceMetaInfo;
import com.whut.rpc.core.model.ServiceRegisterInfo;
import com.whut.rpc.core.registry.BasicRegistry;
import com.whut.rpc.core.registry.LocalRegistry;
import com.whut.rpc.core.registry.RegistryFactory;
import com.whut.rpc.core.server.BasicServer;
import com.whut.rpc.core.server.tcp.vertx.VertxTcpServer;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * provider client starts by this class
 *
 * @author whut2024
 * @since 2024-08-03
 */
@Slf4j
public class ProviderBootstrap {


    /**
     * start provider client web server
     *
     * @param serviceRegisterInfoList some services which are going to be registered
     */
    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList) {
        // init configuration
        RpcApplication.init();
        final RpcConfig rpcConfig = RpcApplication.getConfig();

        // register service
        final RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        final BasicRegistry registry = RegistryFactory.get(registryConfig.getType());
        for (ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {
            // local registry
            LocalRegistry.addService(serviceRegisterInfo.getServiceName(), serviceRegisterInfo.getServiceClass());

            final ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();

            serviceMetaInfo.setName(serviceRegisterInfo.getServiceName());
            serviceMetaInfo.setHost(rpcConfig.getHost());
            serviceMetaInfo.setPort(rpcConfig.getPort());

            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                log.error("service registered unsuccessfully");
                throw new RuntimeException(e);
            }

        }

        // start web server
        BasicServer server = new VertxTcpServer();
        server.start(rpcConfig.getPort());
    }
}
