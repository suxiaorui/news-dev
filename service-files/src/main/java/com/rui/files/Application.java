package com.rui.files;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

import java.util.Date;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/4 17:26
 * @Version 1.0
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,MongoAutoConfiguration.class, MongoDataAutoConfiguration.class} )
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
