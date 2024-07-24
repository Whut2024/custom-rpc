package com.whut.rpc.core.proxy;

import com.whut.rpc.core.config.RpcApplication;

import java.lang.reflect.Proxy;

/**
 * generate service proxy object by factory
 * @author whut2024
 * @since 2024-07-23
 */
public class ServiceProxyFactory {

    /**
     * get rpc proxy object
     */
    public static <T> T getProxy(Class<?> serviceClass) {

        // whether start mock proxy
        if (RpcApplication.getConfig().getMock()) return (T) getMockProxy(serviceClass);


        return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class[]{serviceClass}, new ServiceProxy());
    }


    /**
     * get default mock proxy object
     */
    private static Object getMockProxy(Class<?> serviceClass) {
        return Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class[]{serviceClass}, new MockProxy());
    }
}
