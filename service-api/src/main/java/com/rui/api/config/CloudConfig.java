package com.rui.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/11 19:45
 * @Version 1.0
 */

@Configuration
public class CloudConfig {

    public CloudConfig() {
    }

    /**
     * 会基于OKHttp3的配置来配置RestTemplate
     * @return
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(new OkHttp3ClientHttpRequestFactory());
    }

}