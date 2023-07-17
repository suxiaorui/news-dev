package com.rui.article.controller;

import com.rui.api.BaseController;
import com.rui.api.controller.article.CommentControllerApi;
import com.rui.article.service.CommentPortalService;
import com.rui.grace.result.GraceJSONResult;
import com.rui.pojo.bo.CommentReplyBO;
import com.rui.utils.PagedGridResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.rui.api.service.BaseService.REDIS_ARTICLE_COMMENT_COUNTS;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/17 22:35
 * @Version 1.0
 */

@RestController
public class CommentController extends BaseController implements CommentControllerApi {

    final static Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private CommentPortalService commentPortalService;

    @Override
    public GraceJSONResult createArticle(@Valid CommentReplyBO commentReplyBO,
                                         BindingResult result) {

        // 0. 判断BindingResult是否保存错误的验证信息，如果有，则直接return
        if (result.hasErrors()) {
            Map<String, String> errorMap = getErrors(result);
            return GraceJSONResult.errorMap(errorMap);
        }

        // 1. 根据留言用户的id查询他的昵称，用于存入到数据表进行字段的冗余处理，从而避免多表关联查询的性能影响
        String userId = commentReplyBO.getCommentUserId();

        // 2. 发起restTemplate调用用户服务，获得用户侧昵称
        Set<String> idSet = new HashSet<>();
        idSet.add(userId);
        String nickname = getBasicUserList(idSet).get(0).getNickname();

        // 3. 保存用户评论的信息到数据库
        commentPortalService.createComment(commentReplyBO.getArticleId(),
                commentReplyBO.getFatherId(),
                commentReplyBO.getContent(),
                userId,
                nickname);

        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult commentCounts(String articleId) {

        Integer counts =
                getCountsFromRedis(REDIS_ARTICLE_COMMENT_COUNTS + ":" + articleId);

        return GraceJSONResult.ok(counts);
    }


    @Override
    public GraceJSONResult list(String articleId,
                                Integer page,
                                Integer pageSize) {

        if (page == null) {
            page = COMMON_START_PAGE;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult gridResult = commentPortalService.queryArticleComments(articleId, page, pageSize);
        return GraceJSONResult.ok(gridResult);
    }
}
