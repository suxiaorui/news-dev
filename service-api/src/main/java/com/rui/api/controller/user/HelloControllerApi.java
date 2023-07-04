package com.rui.api.controller.user;

import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/4 17:34
 * @Version 1.0
 */

public interface HelloControllerApi {

    @GetMapping("/hello")
    public Object hello();
}
