package com.rui.article.service.impl;

import com.rui.api.service.BaseService;
import com.rui.article.mapper.ArticleMapper;
import com.rui.article.service.ArticleService;
import com.rui.enums.ArticleAppointType;
import com.rui.enums.ArticleReviewStatus;
import com.rui.enums.YesOrNo;
import com.rui.exception.GraceException;
import com.rui.grace.result.ResponseStatusEnum;
import com.rui.pojo.Article;
import com.rui.pojo.Category;
import com.rui.pojo.bo.NewArticleBO;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.management.GarbageCollectorMXBean;
import java.util.Date;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/13 14:39
 * @Version 1.0
 */

@Service
public class ArticleServiceImpl extends BaseService implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private Sid sid;  //新增文章时，article表的主键，利用Sid生成；

    @Override
    public void createArticle(NewArticleBO newArticleBO, Category category) {

        String articleId = sid.nextShort(); //利用sid生成一个随机字符串，作为article表的id

        Article article = new Article();
        BeanUtils.copyProperties(newArticleBO,article); //属性copy，把newArticleBO中的属性，copy到article对象上去；

        article.setId(articleId); //设置主键

        article.setCategoryId(category.getId()); //设置categoryId
        article.setArticleStatus(ArticleReviewStatus.REVIEWING.type); //刚提交的文章，其文章状态设为"审核中"
        article.setCommentCounts(0); //文章初始评论数量设为0
        article.setReadCounts(0); //文章初始阅读数量设为0
        article.setIsDelete(YesOrNo.NO.type); //文章的"逻辑删除状态"，设为0（即在article表中，代表未删除）
        article.setCreateTime(new Date());
        article.setUpdateTime(new Date());
        /**
         * 如果文章是定时发布的，就设置publish_time为预约发布时间；
         * 如果文章不是定时发布，而是立即发布，就设置publish_time为当前发布时间；
         */
        if (article.getIsAppoint() == ArticleAppointType.TIMING.type){
            article.setPublishTime(newArticleBO.getPublishTime());
        }else if (article.getIsAppoint() == ArticleAppointType.IMMEDIATELY.type){
            article.setPublishTime(new Date());
        }

        //调用Dao层的方法，去插入
        int res = articleMapper.insert(article);
        if (res != 1){//如果插入失败,就抛出一个信息是"创建文章失败，请重试或联系管理员！"的自定义异常;
            GraceException.display(ResponseStatusEnum.ARTICLE_CREATE_ERROR);
        }


    }
}
