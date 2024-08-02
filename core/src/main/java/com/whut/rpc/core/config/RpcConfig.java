package com.whut.rpc.core.config;

import com.whut.rpc.core.fault.retry.RetryStrategyKeys;
import com.whut.rpc.core.fault.tolerant.TolerantStrategyKeys;
import com.whut.rpc.core.loadbalancer.LoadBalanceKeys;
import com.whut.rpc.core.serializer.SerializerKeys;
import lombok.Data;

/**
 * the config of rpc framework
 *
 * @author whut2024
 * @since 2024-07-24
 */

@Data
public class RpcConfig {


    private String name = "whut-rpc";


    private String version = "1.0";


    private String host = "localhost";


    private Integer port = 8080;


    private RegistryConfig registryConfig;


    private Boolean mock = false;


    private String serializer = SerializerKeys.JDK;


    private String loadBalancer = LoadBalanceKeys.ROUND_ROBIN;


    private String retryStrategy = RetryStrategyKeys.FIXED_INTERVAL;


    private String tolerantStrategy = TolerantStrategyKeys.FAIL_OVER;
}
