package com.rui.api.controller.user;

import com.rui.grace.result.GraceJSONResult;
import com.rui.pojo.bo.RegistLoginBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/5 17:20
 * @Version 1.0
 */

@Api(value = "用户信息相关Controller", tags = {"用户信息相关Controller"})
@RequestMapping("user")
public interface UserControllerApi {

    @ApiOperation(value = "获得用户账户信息", notes = "获得用户账户信息", httpMethod = "POST")
    @PostMapping("/getAccountInfo")
    public GraceJSONResult getAccountInfo(@RequestParam String userId);


//    @ApiOperation(value = "一键注册登录接口", notes = "一键注册登录接口", httpMethod = "POST")
//    @PostMapping("/doLogin")
//    public GraceJSONResult doLogin(@RequestBody @Valid RegistLoginBO registLoginBO,
//                                   BindingResult result,
//                                   HttpServletRequest request,
//                                   HttpServletResponse response);


}
