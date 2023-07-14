package com.rui.api.controller.article;

import com.rui.grace.result.GraceJSONResult;
import com.rui.pojo.bo.NewArticleBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.Date;

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

    /**
     * 根据条件，分页查询当前登录用户的，文章列表，接口；
     * @param userId:当前登录用户id；
     * @param keyword：一个查询条件：文章搜索关键字；（可以为空）
     * @param status：一个查询条件：文章的状态；（可以为空）
     * @param startDate：一个查询条件：文章的开始时间；（可以为空）
     * @param endDate：一个查询条件：文章的结束时间；（可以为空）
     * @param page：分页查询，当前页码；
     * @param pageSize：每页条目数
     * @return
     */
    @PostMapping("queryMyList")
    @ApiOperation(value = "查询用户的所有文章列表", notes = "查询用户的所有文章列表", httpMethod = "POST")
    public GraceJSONResult queryMyList(@RequestParam  String userId,
                                       @RequestParam String keyword,
                                       @RequestParam Integer status,
                                       @RequestParam Date startDate,
                                       @RequestParam Date endDate,
                                       @RequestParam Integer page,
                                       @RequestParam Integer pageSize);

    /**
     * 后台的，根据条件，管理员查询所有用户的，文章列表，接口；
     * @param status：一个查询条件：文章的状态；（可以为空）
     * @param page：分页查询，当前页码；
     * @param pageSize：每页条目数
     * @return
     */
    @PostMapping("queryAllList")
    @ApiOperation(value = "管理员查询用户的所有文章列表", notes = "管理员查询用户的所有文章列表", httpMethod = "POST")
    public GraceJSONResult queryAllList(@RequestParam Integer status,
                                        @ApiParam(name = "page", value = "查询下一页的第几页", required = false)
                                        @RequestParam Integer page,
                                        @ApiParam(name = "pageSize", value = "分页的每一页显示的条数", required = false)
                                        @RequestParam Integer pageSize);

    /**
     * 【管理员，人工审核文章，接口】
     * @param articleId：文章id
     * @param passOrNot：审核结果，通过OR不通过；
     *   PS：这些参数名，如果不通过@RequestParam("")的方式指定的话。那么这些参数名，需要和前端对应，不能瞎写；
     * @return
     */
    @PostMapping("doReview")
    @ApiOperation(value = "管理员对文章进行审核通过或者失败", notes = "管理员对文章进行审核通过或者失败", httpMethod = "POST")
    public GraceJSONResult doReview(@RequestParam String articleId,
                                    @RequestParam Integer passOrNot);

    /**
     * 用户删除文章接口；
     * @param userId
     * @param articleId
     * @return
     */
    @PostMapping("/delete")
    @ApiOperation(value = "用户删除文章", notes = "用户删除文章", httpMethod = "POST")
    public GraceJSONResult delete(@RequestParam String userId,
                                  @RequestParam String articleId);

    /**
     * 用户撤回文章接口；
     * @param userId
     * @param articleId
     * @return
     */
    @PostMapping("/withdraw")
    @ApiOperation(value = "用户撤回文章", notes = "用户撤回文章", httpMethod = "POST")
    public GraceJSONResult withdraw(@RequestParam String userId,
                                    @RequestParam String articleId);

}
