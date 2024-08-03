package com.whut.rpc.bootstarter.annotation;

import com.whut.rpc.bootstarter.bootstrap.RpcConsumerBootstrap;
import com.whut.rpc.bootstarter.bootstrap.RpcInitBootStrap;
import com.whut.rpc.bootstarter.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * start rpc service
 *
 * @author whut2024
 * @since 2024-08-03
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcInitBootStrap.class, RpcConsumerBootstrap.class, RpcProviderBootstrap.class})
public @interface EnableRpc {


    /**
     * start web server
     */
    boolean startServer() default true;
}
