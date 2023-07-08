package com.rui.api.interceptors;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/8 18:52
 * @Version 1.0
 */



/**
 * 目前为止：拦截访问【更改/完善用户信息，接口】、【获得用户账户信息，接口】，检查用户登录状态是否OK；
 */

public class AdminTokenInterceptor extends BaseInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //首先，尝试从请求的header中，获取"adminUserId"和"adminUserToken"；
        // 也就是获取，前面在调用【查询admin用户名是否已存在，接口】时设置进前端浏览器cookie中的"uid"和"utoken";
        // PS：前端在发起请求的时候，前端程序会把cookie中"uid"和"utoken"设置进请求头；然后，设置的时候，其名字就分别设置为了"adminUserId"和"adminUserToken"；
        // 所以，这儿别瞎写，一定要和前端写的保持一致；
        // PS：如果这个请求的header中没有"adminUserId"和"adminUserToken"，其获取的就是空；
        String userId = request.getHeader("adminUserId");
        String token = request.getHeader("adminUserToken");

        //调用BaseInterceptor中编写的方法，判断一下请求的header中的"adminUserId"和"adminUserToken"是否OK；
        // 也就是说，判断一个前端用户是否是登录的；
        boolean run = verifyUserIdToken(userId, token,REDIS_ADMIN_TOKEN);


        System.out.println(run);
        //如果上面的verifyUserIdToken()执行没问题，那么就说当前用户是登录的了，
        // 就能走到这一步，直接return true，放行这个请求，让其去访问【更改/完善用户信息，接口】、【获得用户账户信息，接口】；
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
