package com.rui.api.controller.article;

import com.rui.grace.result.GraceJSONResult;
import com.rui.pojo.bo.NewArticleBO;
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
 * @Date 2023/7/13 13:02
 * @Version 1.0
 */


@Api(value = "article文章相关Controller",tags = {"article文章相关Controller"})
@RequestMapping("article")  //设置路由，这个是需要前后端约定好的；
public interface ArticleControllerApi {

    /**
     * 【发表文章，接口】
     * @param newArticleBO：使用NewArticleBO来承接文章数据；
     * @param result
     * @return
     */
    @ApiOperation(value = "用户发表文章", notes = "用户发表文章", httpMethod = "POST")
    @PostMapping("/createArticle") //设置路由，这个是需要前后端约定好的；
    public GraceJSONResult createArticle(@RequestBody @Valid NewArticleBO newArticleBO,
                                      BindingResult result);

}
