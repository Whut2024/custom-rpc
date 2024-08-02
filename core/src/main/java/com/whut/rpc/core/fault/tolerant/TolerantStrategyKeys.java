package com.whut.rpc.core.fault.tolerant;

/**
 * some constant for tolerant strategy
 *
 * @author whut2024
 * @since 2024-08-02
 */
public interface TolerantStrategyKeys {


    String FAIL_BACK = "FAIL_BACK";


    String FAIL_FAST = "FAIL_FAST";


    String FAIL_OVER = "FAIL_OVER";


    String FAIL_SAFE = "FAIL_SAFE";


    /**
     * don't use it as a tolerant type, just a simple variable name
     */
    String SERVICE_META_INFO = "SERVICE_META_INFO";


    /**
     * don't use it as a tolerant type, just a simple variable name
     */
    String RPC_REQUEST = "RPC_REQUEST";
}
