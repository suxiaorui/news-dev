package com.rui.article.service;

import com.rui.pojo.Category;
import com.rui.pojo.bo.NewArticleBO;

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

}
