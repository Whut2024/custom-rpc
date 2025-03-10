package com.whut.rpc.core.registry;

import com.whut.rpc.core.config.RegistryConfig;
import com.whut.rpc.core.model.ServiceMetaInfo;

import java.util.List;

/**
 * the basic interface for custom registry client
 *
 * @author whut2024
 * @since 2024-07-26
 */
public interface BasicRegistry {

    /**
     * init the connection with specified registry center
     */
    void init(RegistryConfig config);


    /**
     * register a service to registry center
     */
    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;


    /**
     * delete a service's registry
     */
    void unregister(ServiceMetaInfo serviceMetaInfo);


    /**
     * discovery a service in registry center
     */
    List<ServiceMetaInfo> discovery(String serviceKey);


    /**
     * destroy the connection with registry center
     */
    void destroy();


    /**
     * when a key is putted or removed, modify the service cache
     */
    void watch(String nodeKey);


    /**
     * start the processor of heart beate test
     */
    void refresh();


    /**
     * increase a service's usage
     */
    void increaseUsage(ServiceMetaInfo serviceMetaInfo);


    /**
     * decrease a service's usage
     */
    void decreaseUsage(ServiceMetaInfo serviceMetaInfo);

}
