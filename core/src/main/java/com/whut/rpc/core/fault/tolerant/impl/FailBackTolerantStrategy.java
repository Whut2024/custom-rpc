package com.whut.rpc.core.fault.tolerant.impl;

import com.whut.rpc.core.fault.tolerant.TolerantStrategy;
import com.whut.rpc.core.model.RpcResponse;

import java.util.Map;

/**
 * exception recover
 *
 * @author whut2024
 * @since 2024-08-02
 */
public class FailBackTolerantStrategy implements TolerantStrategy {


    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) throws Exception {
        return null;
    }
}
