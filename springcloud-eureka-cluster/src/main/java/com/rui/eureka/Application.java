package com.rui.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/19 20:17
 * @Version 1.0
 */


@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,
        MongoAutoConfiguration.class})
@EnableEurekaServer // 开启注册中心
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
