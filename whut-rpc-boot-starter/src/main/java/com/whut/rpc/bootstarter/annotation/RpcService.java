package com.whut.rpc.bootstarter.annotation;

import com.whut.rpc.core.constant.RpcConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * used by provider, auto register the target service to registry center
 *
 * @author whut2024
 * @since 2024-08-03
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcService {


    Class<?> interfaceClass() default void.class;


    String version() default RpcConstant.DEFAULT_SERVICE_VERSION;
}
