package com.whut.rpc.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * some services which are going to be registered
 *
 * @author whut2024
 * @since 2024-08-03
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ServiceRegisterInfo<T> {


    private String serviceName;


    private Class<? extends T> serviceClass;
}
