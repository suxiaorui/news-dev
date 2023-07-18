package com.rui.article.service;

import com.rui.pojo.Category;
import com.rui.pojo.bo.NewArticleBO;
import com.rui.utils.PagedGridResult;

import java.util.Date;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/13 14:36
 * @Version 1.0
 */


public interface ArticleService {

    /**
     * 发布文章
     */
    public void createArticle(NewArticleBO newArticleBO, Category category);


    /**
     * 更新定时发布为即时发布
     */
    public void updateAppointToPublish();

    /**
     * 更新单条文章为即时发布
     */
    public void updateArticleToPublish(String articleId);

    /**
     * 根据条件，分页查询当前登录用户的，文章列表；
     */
    public PagedGridResult queryMyArticleList(String userId, String keyword, Integer status,
                                              Date startDate, Date endDate, Integer page, Integer pageSize);


    /**
     * 更改文章的状态
     */
    public void updateArticleStatus(String articleId, Integer pendingStatus);


    /**
     * 关联文章和gridfs的html文件id
     */
    public void updateArticleToGridFS(String articleId, String articleMongoId);


    /**
     * 管理员查询文章列表
     */
    public PagedGridResult queryAllArticleListAdmin(Integer status, Integer page, Integer pageSize);


    /**
     * 删除文章
     */
    public void deleteArticle(String userId, String articleId);

    /**
     * 撤回文章
     */
    public void withdrawArticle(String userId, String articleId);

}
