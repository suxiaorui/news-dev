package com.rui.article.service.impl;

import com.github.pagehelper.PageHelper;
import com.mongodb.client.gridfs.GridFSBucket;
import com.rui.api.config.RabbitMQConfig;
import com.rui.api.config.RabbitMQDelayConfig;
import com.rui.api.service.BaseService;
import com.rui.article.mapper.ArticleMapper;
import com.rui.article.mapper.ArticleMapperCustom;
import com.rui.article.service.ArticleService;
import com.rui.enums.ArticleAppointType;
import com.rui.enums.ArticleReviewLevel;
import com.rui.enums.ArticleReviewStatus;
import com.rui.enums.YesOrNo;
import com.rui.exception.GraceException;
import com.rui.grace.result.ResponseStatusEnum;
import com.rui.pojo.Article;
import com.rui.pojo.Category;
import com.rui.pojo.bo.NewArticleBO;
import com.rui.utils.DateUtil;
import com.rui.utils.PagedGridResult;
import com.rui.utils.extend.AliTextReviewUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.n3r.idworker.Sid;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import tk.mybatis.mapper.entity.Example;

import java.lang.management.GarbageCollectorMXBean;
import java.util.Date;
import java.util.List;

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
    private ArticleMapperCustom articleMapperCustom;

    @Autowired
    private ArticleService articleService;


    @Autowired
    private AliTextReviewUtils aliTextReviewUtils;

    @Autowired
    private Sid sid;  //新增文章时，article表的主键，利用Sid生成；

    @Transactional
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


        // 发送延迟消息到mq，计算定时发布时间和当前时间的时间差，则为往后延迟的时间
        if (article.getIsAppoint() == ArticleAppointType.TIMING.type) {

            Date endDate = newArticleBO.getPublishTime();
            Date startDate = new Date();

//            int delayTimes = (int)(endDate.getTime() - startDate.getTime());

            System.out.println(DateUtil.timeBetween(startDate, endDate));

            // FIXME: 为了测试方便，写死20s
            int delayTimes = 20 * 1000;

            MessagePostProcessor messagePostProcessor = new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    // 设置消息的持久
                    message.getMessageProperties()
                            .setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    // 设置消息延迟的时间，单位ms毫秒
                    message.getMessageProperties()
                            .setDelay(delayTimes);
                    return message;
                }
            };

            rabbitTemplate.convertAndSend(
                    RabbitMQDelayConfig.EXCHANGE_DELAY,
                    "publish.delay.display",
                    articleId,
                    messagePostProcessor);

            System.out.println("延迟消息-定时发布文章：" + new Date());
        }



        // 通过阿里智能AI实现对文章文本的自动检测（自动审核）
//        String reviewTextResult = aliTextReviewUtils.reviewTextContent(newArticleBO.getContent());
        String reviewTextResult = ArticleReviewLevel.REVIEW.type;

        if (reviewTextResult.equalsIgnoreCase(ArticleReviewLevel.PASS.type)) {
            // 修改当前的文章，状态标记为审核通过
            this.updateArticleStatus(articleId, ArticleReviewStatus.SUCCESS.type);
        } else if (reviewTextResult.equalsIgnoreCase(ArticleReviewLevel.REVIEW.type)) {
            // 修改当前的文章，状态标记为需要人工审核
            this.updateArticleStatus(articleId, ArticleReviewStatus.WAITING_MANUAL.type);
        } else if (reviewTextResult.equalsIgnoreCase(ArticleReviewLevel.BLOCK.type)) {
            // 修改当前的文章，状态标记为审核未通过
            this.updateArticleStatus(articleId, ArticleReviewStatus.FAILED.type);
        }

    }

    @Transactional
    @Override
    public void updateArticleStatus(String articleId, Integer pendingStatus) {
        Example example = new Example(Article.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", articleId);

        Article pendingArticle = new Article();
        pendingArticle.setArticleStatus(pendingStatus);

        int res = articleMapper.updateByExampleSelective(pendingArticle, example);
        if (res != 1) {
            GraceException.display(ResponseStatusEnum.ARTICLE_REVIEW_ERROR);
        }
    }

    @Transactional
    @Override
    public void updateArticleToGridFS(String articleId, String articleMongoId) {
        Article pendingArticle = new Article();
        pendingArticle.setId(articleId);
        pendingArticle.setMongoFileId(articleMongoId);
        articleMapper.updateByPrimaryKeySelective(pendingArticle);
    }



    @Transactional
    @Override
    public void updateAppointToPublish() {

        articleMapperCustom.updateAppointToPublish();

    }

    @Transactional
    @Override
    public void updateArticleToPublish(String articleId) {
        Article article = new Article();
        article.setId(articleId);
        article.setIsAppoint(ArticleAppointType.IMMEDIATELY.type);
        articleMapper.updateByPrimaryKeySelective(article);
    }


    @Override
    public PagedGridResult queryMyArticleList(String userId,
                                              String keyword,
                                              Integer status,
                                              Date startDate,
                                              Date endDate,
                                              Integer page,
                                              Integer pageSize) {

        // 1. 根据参数情况，构建查询条件；
        // 1.1 先创建一个查询实例，这个查询是针对Article作查询的；
        Example example = new Example(Article.class);
        // 1.2 设置排序方式；其中的"createdTime"，指的是Article类中的createTime字段；
        example.orderBy("createTime").desc();
        // 1.3 给上面的查询实例，增加查询条件;
        Example.Criteria criteria = example.createCriteria();
        // 1.3.1 如果前端设置了"搜索关键字"这个条件;那么我们就针对文章的标题，根据这个关键字去模糊查询；
        if (StringUtils.isNotBlank(keyword)) {
            //这儿就不使用andEqualTo()了，而是使用andLike()去模糊查询；然后，后面我们增加了%来匹配；
            criteria.andLike("title", "%" + keyword + "%");
        }
        // 1.3.2 如果前端设置了"用户id"这个条件，我们查询的时候，article表中的publish_user_id字段需要等于前端传的publishUserId；
        if (StringUtils.isNotBlank(userId)) {
            criteria.andEqualTo("publishUserId", userId);
        }
        // 1.3.3.1 如果前端设置了"文章状态"这个条件,并且用户状态是我们规定的那几种状态之一，那么就设置上即可；
        if (ArticleReviewStatus.isArticleStatusValid(status)) {
            criteria.andEqualTo("articleStatus", status);
        }
        // 1.3.3.2 如果前端设置了"文章状态"这个条件,并且用户状态是"12"，那么就需要根据我们这儿的处理逻辑，予以转换；
        if (status != null && status == 12) {
            criteria.andEqualTo("articleStatus", ArticleReviewStatus.REVIEWING.type)
                    .orEqualTo("articleStatus", ArticleReviewStatus.WAITING_MANUAL.type);
        }
        /**
         * 1.3.4 对于那些，用户已经删除了的（逻辑删除，其实该文章在数据库中也还有，只是在用户看来，这篇文章他自己已经删除
         * 了；不过对于这些"用户已经逻辑删除的文章"，后台管理员也还是可以看到的；）
         */
        criteria.andEqualTo("isDelete", YesOrNo.NO.type);
        // 1.3.5 如果前端设置了"文章日期"这个条件;
        if (startDate != null) {
            //那么，使用andGreaterThanOrEqualTo()方法设置，时间需要>=我们传的startDate;
            criteria.andGreaterThanOrEqualTo("createTime", startDate);
        }
        if (endDate != null) {
            //那么，使用andLessThanOrEqualTo()方法设置，时间需要<=我们传的endDate;
            criteria.andLessThanOrEqualTo("publishTime", endDate);
        }

        // 1.4 设置分页;
        PageHelper.startPage(page, pageSize);
        // 1.5 去查询
        List<Article> list = articleMapper.selectByExample(example);
        // 1.6 把查询结果，包装成符合前端要求的pagedGridResult格式的；
        PagedGridResult pagedGridResult = setterPagedGrid(list, page);

        return pagedGridResult;

    }

    @Override
    public PagedGridResult queryAllArticleListAdmin(Integer status, Integer page, Integer pageSize) {
        Example articleExample = new Example(Article.class);
        articleExample.orderBy("createTime").desc();

        Example.Criteria criteria = articleExample.createCriteria();
        if (ArticleReviewStatus.isArticleStatusValid(status)) {
            criteria.andEqualTo("articleStatus", status);
        }

        // 审核中是机审和人审核的两个状态，所以需要单独判断
        if (status != null && status == 12) {
            criteria.andEqualTo("articleStatus", ArticleReviewStatus.REVIEWING.type)
                    .orEqualTo("articleStatus", ArticleReviewStatus.WAITING_MANUAL.type);
        }

        //isDelete 必须是0
        criteria.andEqualTo("isDelete", YesOrNo.NO.type);

        /**
         * page: 第几页
         * pageSize: 每页显示条数
         */
        PageHelper.startPage(page, pageSize);
        List<Article> list = articleMapper.selectByExample(articleExample);
        return setterPagedGrid(list, page);
    }

    @Transactional
    @Override
    public void deleteArticle(String userId, String articleId) {
        Example articleExample = makeExampleCriteria(userId, articleId);

        Article pending = new Article();
        pending.setIsDelete(YesOrNo.YES.type);

        int result = articleMapper.updateByExampleSelective(pending, articleExample);
        if (result != 1) {
            GraceException.display(ResponseStatusEnum.ARTICLE_DELETE_ERROR);
        }

        deleteHTML(articleId);
    }

    @Autowired
    private GridFSBucket gridFSBucket;
    /**
     * 文章撤回删除后，删除静态化的html
     */
    private void deleteHTML(String articleId) {
        // 1. 查询文章的mongoFileId
        Article pending = articleMapper.selectByPrimaryKey(articleId);
        String articleMongoId = pending.getMongoFileId();

        // 2. 删除GridFS上的文件
        gridFSBucket.delete(new ObjectId(articleMongoId));

        // 3. 删除消费端的HTML文件
//        doDeleteArticleHTML(articleId);
        doDeleteArticleHTMLByMQ(articleId);
    }

    @Autowired
    public RestTemplate restTemplate;

    private void doDeleteArticleHTML(String articleId) {
        String url = "http://html.imoocnews.com:8002/article/html/delete?articleId=" + articleId;
        ResponseEntity<Integer> responseEntity = restTemplate.getForEntity(url, Integer.class);
        int status = responseEntity.getBody();
        if (status != HttpStatus.OK.value()) {
            GraceException.display(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;
    private void doDeleteArticleHTMLByMQ(String articleId) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_ARTICLE,
                "article.html.download.do", articleId);
    }

    @Transactional
    @Override
    public void withdrawArticle(String userId, String articleId) {
        Example articleExample = makeExampleCriteria(userId, articleId);

        Article pending = new Article();
        pending.setArticleStatus(ArticleReviewStatus.WITHDRAW.type);

        int result = articleMapper.updateByExampleSelective(pending, articleExample);
        if (result != 1) {
            GraceException.display(ResponseStatusEnum.ARTICLE_WITHDRAW_ERROR);
        }

        deleteHTML(articleId);
    }

    private Example makeExampleCriteria(String userId, String articleId) {
        Example articleExample = new Example(Article.class);
        Example.Criteria criteria = articleExample.createCriteria();
        criteria.andEqualTo("publishUserId", userId);
        criteria.andEqualTo("id", articleId);
        return articleExample;
    }

}


