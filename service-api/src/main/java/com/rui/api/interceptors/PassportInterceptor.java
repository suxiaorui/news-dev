package com.rui.api.interceptors;

import com.rui.exception.GraceException;
import com.rui.grace.result.ResponseStatusEnum;
import com.rui.utils.IPUtil;
import com.rui.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/4 22:48
 * @Version 1.0
 */
public class PassportInterceptor implements HandlerInterceptor {


    @Autowired
    public RedisOperator redis;

    public static final String MOBILE_SMSCODE = "mobile:smscode";

    // 拦截请求，访问controller之前
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获得用户ip
        String userIp = IPUtil.getRequestIp(request);
        boolean keyIsExist = redis.keyIsExist(MOBILE_SMSCODE + ":" + userIp);

        if (keyIsExist) {
            GraceException.display(ResponseStatusEnum.SMS_NEED_WAIT_ERROR);
//            System.out.println("短信发送频率太大！");
            return false;
            /**
             * false：请求被拦截
             * true：请求通过验证，放行
             */

        }
        return true;
    }

        // 请求访问到controller之后，渲染视图之前

        // 请求访问到controller之后，渲染视图之后
}
