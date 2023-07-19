package com.rui.article.controller;

import com.rui.api.config.RabbitMQConfig;
import com.rui.api.controller.user.HelloControllerApi;
import com.rui.grace.result.GraceJSONResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/13 13:01
 * @Version 1.0
 */

@RestController
@RequestMapping("producer")
public class HelloController {


    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/hello")
    public Object hello() {

        /**
         * RabbitMQ 的路由规则 routing key
         *  display.*.* -> * 代表一个占位符
         *      例：
         *          display.do.download  匹配
         *          display.do.upload.done   不匹配
         *
         * display.# -> # 代表任意多个占位符
         *      例:
         *          display.do.download  匹配
         *          display.do.upload.done.over   匹配
         *
         *
         */


//        rabbitTemplate.convertAndSend(
//            RabbitMQConfig.EXCHANGE_ARTICLE,
//            "article.hello",
//            "这是从生产者发送的消息~");

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_ARTICLE,
                "article.publish.download.do",
                "1001");



        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_ARTICLE,
                "article.success.do",
                "1002");

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_ARTICLE,
                "article.play",
                "1003");

        return GraceJSONResult.ok();
    }
}
