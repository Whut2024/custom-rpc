package com.whut.rpc.core.registry;

import com.whut.rpc.core.serializer.SpiLoader;

import static com.whut.rpc.core.registry.RegistryKeys.*;

/**
 * a singleton factory for registry center client
 *
 * @author whut2024
 * @since 2024-07-26
 */
public class RegistryFactory {


    // init all client's class
    static {
        SpiLoader.load(BasicRegistry.class);
    }


    /**
     * a default client
     */
    private final static BasicRegistry DEFAULT_REGISTRY = (BasicRegistry) SpiLoader.get(BasicRegistry.class, ETCD);


    /**
     * @param key  an alias for a client
     */
    public static BasicRegistry get(String key) {
        return (BasicRegistry) SpiLoader.get(BasicRegistry.class, key);
    }


}
