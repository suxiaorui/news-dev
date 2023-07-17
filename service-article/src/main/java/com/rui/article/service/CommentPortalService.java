package com.rui.article.service;

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

}
