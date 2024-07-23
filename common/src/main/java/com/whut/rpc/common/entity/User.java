package com.whut.rpc.common.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {

    private String username;

    private final static long serialVersionUID = 1L;

}
