package com.whut.rpc.consumer;

import com.whut.rpc.common.entity.User;
import com.whut.rpc.common.servcie.UserService;

/**
 * consumer service starter class
 */
public class Starter {

    public static void main(String[] args) {
        // todo   use easy module to create a proxy service object which can invoke methods remotely
        UserService userService = null;

        User user = new User();
        user.setUsername("lq");

        User remoteUser = userService.name(user);


        if (remoteUser == null) {
            System.out.println("remote user is null");
            return;
        }

        System.out.println(user);
    }
}
