package com.whut.rpc.core.fault.retry;

import com.whut.rpc.core.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * the interface for retry strategy
 *
 * @author whut2024
 * @since 2024-08-01
 */
public interface RetryStrategy {


    RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception;
}
