package com.rui.user;

import tk.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/4 17:26
 * @Version 1.0
 */
@SpringBootApplication(exclude = {MongoAutoConfiguration.class} )
@MapperScan(basePackages = "com.rui.user.mapper")
@ComponentScan( basePackages = {"com.rui", "org.n3r.idworker"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
