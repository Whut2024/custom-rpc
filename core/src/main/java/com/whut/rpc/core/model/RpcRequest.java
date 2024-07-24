package com.whut.rpc.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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
public class RpcRequest implements Serializable {

    private String serviceName;


    private String methodName;


    private Class<?>[] argsClassType;


    private Object[] args;


    private final static long serialVersionUID = 1L;
}
