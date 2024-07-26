package com.whut.rpc.core.config;

import lombok.Data;

/**
 * the config for registry enter
 * @author whut2024
 * @since 2024-07-26
 */

@Data
public class RegistryConfig {

    /**
     * the type of registry center(an alias)
     */
    private String type;


    private String address;


    private String username;


    private String password;


    private Long timeout;


}
