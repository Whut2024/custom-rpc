package com.whut.rpc.core.loadbalancer;

import com.whut.rpc.core.util.SpiLoader;

import static com.whut.rpc.core.loadbalancer.LoadBalanceKeys.*;

/**
 * singleton factory
 *
 * @author whut2024
 * @since 2024-07-31
 */
public class LoadBalancerFactory {


    // init
    static {
        SpiLoader.load(LoadBalancer.class);
    }

    /**
     * default load balancer
     */
    public final static LoadBalancer DEFAULT_LOAD_BALANCER = get(ROUND_ROBIN);


    /**
     * get method
     */
    public static LoadBalancer get(String alias) {
        return (LoadBalancer) SpiLoader.get(LoadBalancer.class, alias);
    }

}
