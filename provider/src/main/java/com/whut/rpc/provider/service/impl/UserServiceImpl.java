package com.whut.rpc.provider.service.impl;

import com.whut.rpc.common.entity.User;
import com.whut.rpc.common.servcie.UserService;

public class UserServiceImpl implements UserService {
    @Override
    public User name(User user) {
        System.out.println("用户名: " + user.getUsername());

        return user;
    }
}
