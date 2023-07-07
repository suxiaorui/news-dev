package com.rui.files;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/7 17:01
 * @Version 1.0
 */


@Component
@PropertySource("classpath:file-${spring.profiles.active}.properties")
@ConfigurationProperties(prefix = "file")
public class FileResource {

    private String host;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
