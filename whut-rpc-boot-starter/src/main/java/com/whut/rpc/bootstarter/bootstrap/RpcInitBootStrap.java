package com.whut.rpc.bootstarter.bootstrap;

import com.whut.rpc.bootstarter.annotation.EnableRpc;
import com.whut.rpc.core.config.RpcApplication;
import com.whut.rpc.core.config.RpcConfig;
import com.whut.rpc.core.server.BasicServer;
import com.whut.rpc.core.server.tcp.vertx.VertxTcpServer;
import com.whut.rpc.core.util.ConfigUtil;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import static com.whut.rpc.core.constant.RpcConstant.DEFAULT_CONFIG_PREFIX;

/**
 * start service according to @EnableRpc
 *
 * @author whut2024
 * @since 2024-08-03
 */
public class RpcInitBootStrap implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        boolean startServer = (boolean) importingClassMetadata.getAnnotationAttributes(EnableRpc.class.getName()).get("startServer");


        // whether start sever, heart beat and stop-destroy
        final RpcConfig rpcConfig = ConfigUtil.loadConfig(DEFAULT_CONFIG_PREFIX, RpcConfig.class);
        rpcConfig.setSingleConsumer(!startServer);
        RpcApplication.init(rpcConfig);

        if (startServer) {
            new VertxTcpServer().start(rpcConfig.getPort());
        }

    }
}
