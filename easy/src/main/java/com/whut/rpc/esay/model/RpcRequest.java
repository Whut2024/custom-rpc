package com.whut.rpc.esay.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author whut2024
 * @since 2024-07-23
 *
 * the message rpc invoking request should send
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest {

    private String serviceName;


    private String methodName;


    private Class<?>[] argsClassType;


    private Object[] args;
}
