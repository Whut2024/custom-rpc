package com.whut.rpc.core.fault.retry.impl;

import com.whut.rpc.core.fault.retry.RetryStrategy;
import com.whut.rpc.core.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * don't retry after service invoking failed
 *
 * @author whut2024
 * @since 2024-08-01
 */
public class NoRetryStrategy implements RetryStrategy {


    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        // just invoke service one time
        return callable.call();
    }
}
