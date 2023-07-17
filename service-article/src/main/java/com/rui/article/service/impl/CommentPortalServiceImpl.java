package com.rui.article.service.impl;

import com.rui.api.service.BaseService;
import com.rui.article.mapper.CommentsMapper;
import com.rui.article.service.ArticlePortalService;
import com.rui.article.service.CommentPortalService;
import com.rui.pojo.Comments;
import com.rui.pojo.vo.ArticleDetailVO;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/17 22:40
 * @Version 1.0
 */

@Service
public class CommentPortalServiceImpl extends BaseService implements CommentPortalService {

    @Autowired
    private Sid sid;

    @Autowired
    private ArticlePortalService articlePortalService;


    @Autowired
    private CommentsMapper commentsMapper;

    @Transactional
    @Override
    public void createComment(String articleId, String fatherCommentId, String content, String userId, String nickname) {
        String commentId = sid.nextShort();

        ArticleDetailVO article
                = articlePortalService.queryDetail(articleId);

        Comments comments = new Comments();
        comments.setId(commentId);

        comments.setWriterId(article.getPublishUserId());
        comments.setArticleTitle(article.getTitle());
        comments.setArticleCover(article.getCover());
        comments.setArticleId(articleId);

        comments.setFatherId(fatherCommentId);
        comments.setCommentUserId(userId);
        comments.setCommentUserNickname(nickname);

        comments.setContent(content);
        comments.setCreateTime(new Date());

        commentsMapper.insert(comments);

        // 评论数累加
        redis.increment(REDIS_ARTICLE_COMMENT_COUNTS + ":" + articleId, 1);


    }
}
