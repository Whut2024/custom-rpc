package com.whut.rpc.bootstarter.bootstrap;

import cn.hutool.core.util.StrUtil;
import com.whut.rpc.bootstarter.annotation.RpcService;
import com.whut.rpc.core.config.RpcApplication;
import com.whut.rpc.core.config.RpcConfig;
import com.whut.rpc.core.constant.RpcConstant;
import com.whut.rpc.core.model.ServiceMetaInfo;
import com.whut.rpc.core.registry.BasicRegistry;
import com.whut.rpc.core.registry.LocalRegistry;
import com.whut.rpc.core.registry.RegistryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * provider starts service, according to @RpcService, instead of package scanning, it uses post processors
 *
 * @author whut2024
 * @since 2024-08-03
 */
public class RpcProviderBootstrap implements BeanPostProcessor {


    private static final Logger log = LoggerFactory.getLogger(RpcProviderBootstrap.class);
    private final RpcConfig RPC_CONFIG = RpcApplication.getConfig();

    private final BasicRegistry REGISTRY = RegistryFactory.get(RPC_CONFIG.getRegistryConfig().getType());


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        RpcService rpcService = beanClass.getAnnotation(RpcService.class);

        if (rpcService == null) return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);

        // register service
        Class<?> interfaceClass = rpcService.interfaceClass();
        if (interfaceClass == void.class) interfaceClass = beanClass.getInterfaces()[0];
        String serviceName = interfaceClass.getName();

        // locally register
        LocalRegistry.addService(serviceName, beanClass);

        String version = rpcService.version();
        if (StrUtil.isBlank(version)) version = RpcConstant.DEFAULT_SERVICE_VERSION;

        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setName(serviceName);
        serviceMetaInfo.setVersion(version);
        serviceMetaInfo.setHost(RPC_CONFIG.getHost());
        serviceMetaInfo.setPort(RPC_CONFIG.getPort());

        // remotely register
        try {
            REGISTRY.register(serviceMetaInfo);
        } catch (Exception e) {
            log.error("register service unsuccessfully");
            throw new RuntimeException(e);
        }


        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);


    }
}
