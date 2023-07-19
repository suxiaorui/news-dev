package com.rui.article;

import com.rule.MyRule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = "com.rui.article.mapper")
@ComponentScan(basePackages = {"com.rui", "org.n3r.idworker"})
@EnableEurekaClient
//@RibbonClient(name = "SERVICE-USER", configuration = MyRule.class)
@EnableFeignClients({"com.rui"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
