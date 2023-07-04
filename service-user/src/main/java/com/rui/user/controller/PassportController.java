package com.rui.user.controller;

import com.rui.api.controller.user.PassportControllerApi;
import com.rui.grace.result.GraceJSONResult;
import com.rui.utils.SMSUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/4 21:30
 * @Version 1.0
 */

@RestController
public class PassportController implements PassportControllerApi  {

    final static Logger logger = LoggerFactory.getLogger(PassportController.class);

    @Autowired
    private SMSUtils smsUtils;

    @Override
    public GraceJSONResult getSMSCode(String mobile, HttpServletRequest request) {

        String code = "123456";
        try {
            smsUtils.sendSMS(mobile,code);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return GraceJSONResult.ok();
    }
}
