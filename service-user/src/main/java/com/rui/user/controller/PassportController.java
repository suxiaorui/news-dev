package com.rui.user.controller;

import com.rui.api.BaseController;
import com.rui.api.controller.user.PassportControllerApi;
import com.rui.grace.result.GraceJSONResult;
import com.rui.utils.IPUtil;
import com.rui.utils.SMSUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/4 21:30
 * @Version 1.0
 */

@RestController
public class PassportController extends BaseController implements PassportControllerApi  {

    final static Logger logger = LoggerFactory.getLogger(PassportController.class);

    @Autowired
    private SMSUtils smsUtils;


    @Override
    public GraceJSONResult getSMSCode(String mobile, HttpServletRequest request) {

        //获得用户ip
        String userIp = IPUtil.getRequestIp(request);

        // 根据用户的ip进行限制，限制用户在60s内只能获得一次验证码
        redis.setnx60s(MOBILE_SMSCODE + ":" + userIp, userIp);

        //生成随机验证码并且发送短信
        String random = (int)((Math.random() * 9 + 1) * 100000) + "";

//        try {
//            smsUtils.sendSMS(mobile,random);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        // 将验证码存入redis，用于后续验证
        redis.set(MOBILE_SMSCODE + ":" + mobile, random,30 * 60);

        return GraceJSONResult.ok();
    }
}
