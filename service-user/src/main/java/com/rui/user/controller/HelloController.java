package com.rui.user.controller;

import com.rui.api.controller.user.HelloControllerApi;
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


    //    Swagger2 文档生成工具
    public Object hello() {

        logger.debug("debug: hello~");
        logger.info("info: hello~");
        logger.warn("warn: hello~");
        logger.error("error: hello~");

        return "hello";
//        return IMOOCJSONResult.ok();
//        return IMOOCJSONResult.ok("hello");
//        return IMOOCJSONResult.errorMsg("您的信息有误~！");

    }
}
