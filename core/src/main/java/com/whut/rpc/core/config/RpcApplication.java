package com.whut.rpc.core.config;

import com.whut.rpc.core.constant.RpcConstant;
import com.whut.rpc.core.registry.BasicRegistry;
import com.whut.rpc.core.registry.RegistryFactory;
import com.whut.rpc.core.util.ConfigUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * the holder of rpc config
 *
 * @author whut2024
 * @since 2024-07-24
 */

@Slf4j
public class RpcApplication implements RpcConstant {

    volatile private static RpcConfig rpcConfig;


    /**
     * load config file or use default config
     */
    public static void init() {
        RpcConfig newRpcConfig;
        try {
            newRpcConfig = ConfigUtil.loadConfig(DEFAULT_CONFIG_PREFIX, RpcConfig.class);
        } catch (Exception e) {
            log.error("default rpc config file loading failed, use default rpc config");

            // use default config
            newRpcConfig = new RpcConfig();
        }

        init(newRpcConfig);
    }


    /**
     * support inject config by yourself
     */
    public static void init(RpcConfig rpcConfig) {
        RpcApplication.rpcConfig = rpcConfig;

        // init registry center client (build connection)
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        String registryClientType = registryConfig.getType();
        BasicRegistry registry = RegistryFactory.get(registryClientType);
        registry.init(registryConfig);

        log.info("rpc init, the config is {}", rpcConfig);


        // add (registry client) destroy method to shut-down-hook
        Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));
    }


    /**
     * get the rpc config
     */
    public static RpcConfig getConfig() {
        if (rpcConfig == null) {
            synchronized(RpcApplication.class) {
                if (rpcConfig == null) init();
            }
        }

        return rpcConfig;
    }
}
