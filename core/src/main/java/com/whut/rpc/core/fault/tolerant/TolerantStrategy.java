package com.whut.rpc.core.fault.tolerant;

import com.whut.rpc.core.model.RpcResponse;

import java.util.Map;

/**
 * the interface for tolerant strategy
 *
 * @author whut2024
 * @since 2024-08-02
 */
public interface TolerantStrategy {


    /**
     * invoke tolerant strategy
     * @param context params, the first param is service-meta-info
     * @param e an exception when it processes
     */
    RpcResponse doTolerant(Map<String, Object> context, Exception e) throws Exception;


}
