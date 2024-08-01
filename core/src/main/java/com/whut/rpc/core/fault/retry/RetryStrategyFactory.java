package com.whut.rpc.core.fault.retry;

import com.whut.rpc.core.serializer.SpiLoader;

import static com.whut.rpc.core.fault.retry.RetryStrategyKeys.*;

/**
 * singleton factory
 *
 * @author whut2024
 * @since 2024-08-01
 */
public class RetryStrategyFactory {

    static {
        SpiLoader.load(RetryStrategy.class);
    }



    public final static RetryStrategy DEFAULT_RETRY_STRATEGY = get(FIXED_INTERVAL);



    public static RetryStrategy get(String alias) {
        return (RetryStrategy) SpiLoader.get(RetryStrategy.class, alias);
    }
}
