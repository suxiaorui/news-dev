package com.rui.api.controller.admin;

import com.rui.grace.result.GraceJSONResult;
import com.rui.pojo.bo.SaveFriendLinkBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/11 20:09
 * @Version 1.0
 */

@Api(value = "首页友情链接维护", tags = {"首页友情链接维护controller"})
@RequestMapping("friendLinkMng")
public interface FriendLinkControllerApi {

    @ApiOperation(value = "新增或者修改友情链接", notes = "新增或者修改友情链接", httpMethod = "POST")
    @PostMapping("/saveOrUpdateFriendLink")
    public GraceJSONResult saveOrUpdateFriendLink(
            @RequestBody @Valid SaveFriendLinkBO saveFriendLinkBO);
//            BindingResult result);

    @ApiOperation(value = "查询友情链接列表", notes = "查询友情链接列表", httpMethod = "POST")
    @PostMapping("/getFriendLinkList")
    public GraceJSONResult getFriendLinkList();

    @ApiOperation(value = "删除友情链接", notes = "删除友情链接", httpMethod = "POST")
    @PostMapping("/delete")
    public GraceJSONResult delete(@RequestParam String linkId);


    @ApiOperation(value = "门户端查询友情链接列表", notes = "门户端查询友情链接列表", httpMethod = "GET")
    @GetMapping("portal/list")
    public GraceJSONResult queryPortalAllFriendLinkList();
}
