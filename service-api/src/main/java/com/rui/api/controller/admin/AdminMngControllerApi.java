package com.rui.api.controller.admin;

import com.rui.grace.result.GraceJSONResult;
import com.rui.pojo.bo.AdminLoginBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/7 19:44
 * @Version 1.0
 */


@Api(value = "管理员admin维护相关Controller",tags = {"管理员admin维护相关Controller"})
@RequestMapping("adminMng") //设置路由
public interface AdminMngControllerApi {


    /**
     * 【管理员用户登录，接口】
     * @param adminLoginBO :使用AdminLoginBO，来承接用户登录时的信息；
     * @return
     */

    @ApiOperation(value = "管理员用户登录", notes = "管理员用户登录", httpMethod = "POST")
    @PostMapping("/adminLogin")
    public GraceJSONResult adminLogin(@RequestBody @Valid AdminLoginBO adminLoginBO,
                                      BindingResult result,
                                      HttpServletRequest request,
                                      HttpServletResponse response);






}
