package com.rui.config.controller;

import com.rui.grace.result.GraceJSONResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/21 13:00
 * @Version 1.0
 */

@RestController
public class HelloController {

    @GetMapping("/hello")
    public Object hello() {
        return GraceJSONResult.ok();
    }

}
