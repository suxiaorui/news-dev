package com.rui.article.html.controller;

import com.rui.api.controller.user.HelloControllerApi;
import com.rui.grace.result.GraceJSONResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/18 18:31
 * @Version 1.0
 */


@RestController
public class HelloController implements HelloControllerApi {

    final static Logger logger = LoggerFactory.getLogger(HelloController.class);

    public Object hello() {
        return GraceJSONResult.ok();
    }

}
