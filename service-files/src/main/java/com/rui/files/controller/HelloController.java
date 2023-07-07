package com.rui.files.controller;

import com.rui.api.controller.user.HelloControllerApi;
import com.rui.grace.result.GraceJSONResult;
import com.rui.grace.result.RuiJSONResult;
import com.rui.utils.RedisOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    final static Logger logger = LoggerFactory.getLogger(HelloController.class);


    public Object hello()  {
        return GraceJSONResult.ok("hello world");
    }

}
