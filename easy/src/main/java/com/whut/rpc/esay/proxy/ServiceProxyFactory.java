package com.whut.rpc.esay.proxy;

import java.lang.reflect.Proxy;

/**
 * @author whut2024
 * @since 2024-07-23
 *
 * generate service proxy object by factory
 */
public class ServiceProxyFactory {

    public static <T> T getProxy(Class<?> serviceClass) {
        return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class[]{serviceClass}, new ServiceProxy());
    }
}
