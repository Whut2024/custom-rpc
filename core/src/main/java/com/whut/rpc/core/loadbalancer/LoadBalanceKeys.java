package com.whut.rpc.core.loadbalancer;

/**
 * some constant for load balancer
 * @author whut2024
 * @since 2024-07-31
 */
public interface LoadBalanceKeys {


    String CONSISTENT_HASH = "CONSISTENT_HASH";


    String RANDOM = "RANDOM";


    String ROUND_ROBIN = "ROUND_ROBIN";


    String LEAST_USAGE = "LEAST_USAGE";

}
