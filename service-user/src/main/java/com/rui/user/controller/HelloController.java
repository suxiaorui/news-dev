package com.rui.user.controller;

import com.rui.api.controller.user.HelloControllerApi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/4 17:16
 * @Version 1.0
 */

@RestController
public class HelloController implements HelloControllerApi {

    public Object hello(){
        return "hello";

    }
}
