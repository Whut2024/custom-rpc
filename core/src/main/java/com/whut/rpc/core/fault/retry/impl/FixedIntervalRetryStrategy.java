package com.whut.rpc.core.fault.retry.impl;

import com.github.rholder.retry.*;
import com.whut.rpc.core.fault.retry.RetryStrategy;
import com.whut.rpc.core.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * retry in a fixed interval
 *
 * @author whut2024
 * @since 2024-08-01
 */
@Slf4j
public class FixedIntervalRetryStrategy implements RetryStrategy {


    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        final Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfExceptionOfType(Exception.class)
                .withWaitStrategy(WaitStrategies.fixedWait(3L, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.warn("the retry times is {}", attempt.getAttemptNumber());
                    }
                })
                .build();


        return retryer.call(callable);
    }
}
