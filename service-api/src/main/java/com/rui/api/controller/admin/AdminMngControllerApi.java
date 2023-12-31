package com.rui.api.controller.admin;

import com.rui.grace.result.GraceJSONResult;
import com.rui.pojo.bo.AdminLoginBO;
import com.rui.pojo.bo.NewAdminBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    /**
     * 查询admin用户名，是否已存在;
     * @param username
     * @return
     */
    @ApiOperation(value = "查询admin用户名，是否已存在", notes = "查询admin用户名，是否已存在", httpMethod = "POST")
    @PostMapping("/adminIsExist") //设置路由，这个是需要前后端约定好的；
    public GraceJSONResult adminIsExist(@RequestParam String username);



    /**
     *【新增admin账号，接口】
     * @param newAdminBO :NewAdminBO，来承接创建管理员用户时的信息；
     * @param result
     * @param request
     * @param response
     * @return
     */

    @ApiOperation(value = "创建admin账号", notes = "创建admin账号", httpMethod = "POST")
    @PostMapping("/addNewAdmin") //设置路由，这个是需要前后端约定好的；
    public GraceJSONResult addNewAdmin(@RequestBody @Valid NewAdminBO newAdminBO,
                                       BindingResult result,
                                       HttpServletRequest request,
                                       HttpServletResponse response);

    /**
     * 【分页查询admin账号列表，接口】;
     *
     * @param page：想要查询第几页；
     * @param pageSize：每一页的条目数；
     * @return
     */
    @ApiOperation(value = "查询admin列表", notes = "查询admin列表", httpMethod = "POST")
    @PostMapping("/getAdminList") //设置路由，这个是需要前后端约定好的；
    public GraceJSONResult getAdminList(@RequestParam @ApiParam(name = "page", value = "想要查询第几页", required = false)
                                                Integer page,
                                        @RequestParam @ApiParam(name = "pageSize", value = "每一页的条目数", required = false)
                                                Integer pageSize);


    /**
     * 【admin管理员退出登录，接口】;
     * @param adminId
     * @param request
     * @param response
     * @return
     */
    @ApiOperation(value = "admin管理员退出登录", notes = "admin管理员退出登录", httpMethod = "POST")
    @PostMapping("/adminLogout") //设置路由，这个是需要前后端约定好的；
    public GraceJSONResult adminLogout(@RequestParam String adminId,
                                       HttpServletRequest request,
                                       HttpServletResponse response);

    @ApiOperation(value = "管理员人脸登录", notes = "管理员人脸登录", httpMethod = "POST")
    @PostMapping("adminFaceLogin")
    public GraceJSONResult adminFaceLogin(@RequestBody AdminLoginBO adminLoginBO,
                                          HttpServletRequest request,
                                          HttpServletResponse response);



}
