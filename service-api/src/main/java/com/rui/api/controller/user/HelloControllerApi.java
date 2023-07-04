package com.rui.api.controller.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/4 17:34
 * @Version 1.0
 */

@Api(value = "controller的标题",tags = {"xx功能的controller"})
public interface HelloControllerApi {


    @ApiOperation(value = "hello方法的接口",notes = "hello方法的接口",httpMethod = "GET")
    @GetMapping("/hello")
    public Object hello();
}
