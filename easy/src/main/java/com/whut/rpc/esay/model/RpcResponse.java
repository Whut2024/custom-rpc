package com.whut.rpc.esay.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author whut2024
 * @since 2024-07-23
 *
 * the message rpc invoking response should send
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse implements Serializable {

    private Object responseData;


    private Class<?> responseDataClassType;


    private String responseMessage;


    private Exception exception;


    private final static long serialVersionUID = 1L;
}
