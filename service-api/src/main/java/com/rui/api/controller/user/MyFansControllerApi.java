package com.rui.api.controller.user;

import com.rui.grace.result.GraceJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/17 21:05
 * @Version 1.0
 */

@Api(value = "粉丝管理", tags = {"粉丝管理功能的controller"})
@RequestMapping("fans")
public interface MyFansControllerApi {

    @ApiOperation(value = "查询当前用户是否关注作家", notes = "查询当前用户是否关注作家", httpMethod = "POST")
    @PostMapping("/isMeFollowThisWriter")
    public GraceJSONResult isMeFollowThisWriter(@RequestParam String writerId,
                                                @RequestParam String fanId);


    @ApiOperation(value = "用户关注作家，成为粉丝", notes = "用户关注作家，成为粉丝", httpMethod = "POST")
    @PostMapping("/follow")
    public GraceJSONResult follow(@RequestParam String writerId,
                                  @RequestParam String fanId);


    @ApiOperation(value = "取消关注，作家损失粉丝", notes = "取消关注，作家损失粉丝", httpMethod = "POST")
    @PostMapping("/unfollow")
    public GraceJSONResult unfollow(@RequestParam String writerId,
                                    @RequestParam String fanId);

}
