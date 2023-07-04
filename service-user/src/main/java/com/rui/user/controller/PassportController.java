package com.rui.user.controller;

import com.rui.api.BaseController;
import com.rui.api.controller.user.PassportControllerApi;
import com.rui.enums.UserStatus;
import com.rui.grace.result.GraceJSONResult;
import com.rui.grace.result.ResponseStatusEnum;
import com.rui.pojo.AppUser;
import com.rui.pojo.bo.RegistLoginBO;
import com.rui.user.service.UserService;
import com.rui.utils.IPUtil;
import com.rui.utils.SMSUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private UserService userService;


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

    @Override
    public GraceJSONResult doLogin(RegistLoginBO registLoginBO, BindingResult result) {

        // 判断BindingResult中是否保存了错误的验证信息，如果有，则需要返回
        if (result.hasErrors()){
            Map<String, String> map = getErrors(result);
            return GraceJSONResult.errorMap(map);
        }

        String mobile = registLoginBO.getMobile();
        String smsCode = registLoginBO.getSmsCode();

        // 校验验证码是否匹配
        String redisSMSCode = redis.get(MOBILE_SMSCODE + ":" + mobile);
        if (StringUtils.isBlank(redisSMSCode) || !redisSMSCode.equalsIgnoreCase(smsCode)){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }

        // 查询数据库,判断该用户是否注册
        AppUser user = userService.queryMobileIsExist(mobile);
        if (user != null && user.getActiveStatus() == UserStatus.FROZEN.type){
            //如果用户不为空，并且状态为冻结，则直接抛出异常，禁止登录
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_FROZEN);
        }else if (user == null){
            // 如果用户没有注册过，则为null，需要注册信息入库
            user = userService.createUser(mobile);
        }

        return GraceJSONResult.ok(user);
    }


}
