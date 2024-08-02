package com.whut.rpc.core.fault.tolerant.impl;

import com.whut.rpc.core.fault.tolerant.TolerantStrategy;
import com.whut.rpc.core.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * don't try to tolerant it
 * @author whut2024
 * @since 2024-08-02
 */
@Slf4j
public class FailFastTolerantStrategy implements TolerantStrategy {


    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) throws Exception {
        log.error("fail fast, the cause is {}", e.getMessage());
        throw e;
    }
}
