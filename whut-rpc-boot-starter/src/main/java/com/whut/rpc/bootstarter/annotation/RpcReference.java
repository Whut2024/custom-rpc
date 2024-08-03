package com.whut.rpc.bootstarter.annotation;

import com.whut.rpc.core.constant.RpcConstant;
import com.whut.rpc.core.fault.retry.RetryStrategyKeys;
import com.whut.rpc.core.fault.tolerant.TolerantStrategyKeys;
import com.whut.rpc.core.loadbalancer.LoadBalanceKeys;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * used by consumer, auto proxy a service for the target variable
 *
 * @author whut2024
 * @since 2024-08-03
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcReference {


    Class<?> interfaceClass() default void.class;


    String version() default RpcConstant.DEFAULT_SERVICE_VERSION;


    String retryStrategy() default RetryStrategyKeys.FIXED_INTERVAL;


    String tolerantStrategy() default TolerantStrategyKeys.FAIL_OVER;


    String loadBalancer() default LoadBalanceKeys.ROUND_ROBIN;


    boolean mock() default false;
}
