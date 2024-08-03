package com.whut.rpc.bootstarter.bootstrap;

import com.whut.rpc.bootstarter.annotation.RpcReference;
import com.whut.rpc.core.proxy.ServiceProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

/**
 * after initialization, inject proxy object
 *
 * @author whut2024
 * @since 2024-08-03
 */
public class RpcConsumerBootstrap implements BeanPostProcessor {
    private static final Logger log = LoggerFactory.getLogger(RpcConsumerBootstrap.class);

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();

        for (Field field : beanClass.getDeclaredFields()) {
            RpcReference rpcReference = field.getAnnotation(RpcReference.class);
            if (rpcReference == null) continue;

            // this field needs to be injected a proxy object
            Class<?> interfaceClass = rpcReference.interfaceClass();
            if (interfaceClass == null) interfaceClass = field.getType();

            field.setAccessible(true);
            Object object = ServiceProxyFactory.getProxy(interfaceClass);

            try {
                field.set(bean, object);
                field.setAccessible(false);
            } catch (IllegalAccessException e) {
                log.error("inject field unsuccessfully");
                throw new RuntimeException(e);
            }
        }

        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
