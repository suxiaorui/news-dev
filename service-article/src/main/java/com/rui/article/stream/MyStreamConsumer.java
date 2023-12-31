package com.rui.article.stream;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/21 15:36
 * @Version 1.0
 */


import com.rui.pojo.AppUser;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

/**
 * 构建消费端
 */
@Component
@EnableBinding(MyStreamChannel.class)
public class MyStreamConsumer {

    /**
     * 监听并且实现消息的消费和相关业务处理
     */
//    @StreamListener(MyStreamChannel.INPUT)
//    public void receiveMsg(AppUser user) {
//        System.out.println(user.toString());
//    }

    @StreamListener(MyStreamChannel.INPUT)
    public void receiveMsg(String dumpling) {
        System.out.println(dumpling);
    }

}