package com.rui.api.interceptors;

import com.rui.exception.GraceException;
import com.rui.grace.result.ResponseStatusEnum;
import com.rui.utils.IPUtil;
import com.rui.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.rui.api.interceptors.BaseInterceptor.REDIS_USER_TOKEN;


/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/5 19:32
 * @Version 1.0
 */
public class UserTokenInterceptor extends BaseInterceptor implements HandlerInterceptor {


    @Autowired
    public RedisOperator redis;

    public static final String MOBILE_SMSCODE = "mobile:smscode";

    // 拦截请求，访问controller之前
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String userId = request.getHeader("headerUserId");
        String userToken = request.getHeader("headerUserToken");

        // 判断是否放行
        boolean run = verifyUserIdToken(userId,userToken,REDIS_USER_TOKEN);
//        System.out.println(run);
            /**
             * false：请求被拦截
             * true：请求通过验证，放行
             */
        return true;
        }

        // 请求访问到controller之后，渲染视图之前

        // 请求访问到controller之后，渲染视图之后
}
