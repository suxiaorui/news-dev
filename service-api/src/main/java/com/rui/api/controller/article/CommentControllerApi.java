package com.rui.api.controller.article;

import com.rui.grace.result.GraceJSONResult;
import com.rui.pojo.bo.CommentReplyBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/17 22:34
 * @Version 1.0
 */

@Api(value = "评论相关业务的controller", tags = {"评论相关业务的controller"})
@RequestMapping("comment")
public interface CommentControllerApi {


    @PostMapping("createComment")
    @ApiOperation(value = "用户评论", notes = "用户评论", httpMethod = "POST")
    public GraceJSONResult createArticle(@RequestBody @Valid CommentReplyBO commentReplyBO,
                                         BindingResult result);

}
