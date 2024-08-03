package com.whut.rpc.provider;

import com.whut.rpc.common.servcie.UserService;
import com.whut.rpc.core.bootstrap.ProviderBootstrap;
import com.whut.rpc.core.config.RpcApplication;
import com.whut.rpc.core.config.RpcConfig;
import com.whut.rpc.core.model.ServiceMetaInfo;
import com.whut.rpc.core.model.ServiceRegisterInfo;
import com.whut.rpc.core.registry.BasicRegistry;
import com.whut.rpc.core.registry.LocalRegistry;
import com.whut.rpc.core.registry.RegistryFactory;
import com.whut.rpc.core.server.http.vertx.VertxServer;
import com.whut.rpc.core.server.tcp.vertx.VertxTcpServer;
import com.whut.rpc.provider.service.impl.UserServiceImpl;

import java.util.Collections;
import java.util.List;

import static com.whut.rpc.core.registry.RegistryKeys.*;

/**
 * service starter class
 */
public class Starter {

    public static void main(String[] args) throws Exception {
        ServiceRegisterInfo<UserService> serviceServiceRegisterInfo = new ServiceRegisterInfo<>(UserService.class.getName(), UserServiceImpl.class);

        List<ServiceRegisterInfo<?>> serviceRegisterInfoList = Collections.singletonList(serviceServiceRegisterInfo);
        ProviderBootstrap.init(serviceRegisterInfoList);
    }
}
