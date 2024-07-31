package com.whut.rpc.core.config;

import com.whut.rpc.core.loadbalancer.LoadBalanceKeys;
import lombok.Data;

import static com.whut.rpc.core.loadbalancer.LoadBalanceKeys.*;
import static com.whut.rpc.core.serializer.SerializerKeys.*;

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


    private String serializer = JDK;


    private String loadBalancer = ROUND_ROBIN;
}
