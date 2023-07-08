package com.rui.admin.controller;

import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/8 16:48
 * @Version 1.0
 */


public class PWDTest {

    public static void main(String[] args) {
        String pwd = BCrypt.hashpw("admin", BCrypt.gensalt());
        System.out.println(pwd);
    }

}
