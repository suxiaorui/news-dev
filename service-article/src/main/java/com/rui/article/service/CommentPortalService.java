package com.rui.article.service;

import com.rui.utils.PagedGridResult;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/17 22:40
 * @Version 1.0
 */


public interface CommentPortalService {

    /**
     * 发表评论
     */
    public void createComment(String articleId,
                              String fatherCommentId,
                              String content,
                              String userId,
                              String nickname);


    /**
     * 查询文章评论列表
     */
    public PagedGridResult queryArticleComments(String articleId,
                                                Integer page,
                                                Integer pageSize);

    /**
     * 查询我的评论管理列表
     */
    public PagedGridResult queryWriterCommentsMng(String writerId, Integer page, Integer pageSize);


    /**
     * 删除评论
     */
    public void deleteComment(String writerId, String commentId);
}
