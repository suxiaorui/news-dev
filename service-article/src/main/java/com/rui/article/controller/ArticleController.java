package com.rui.article.controller;

import com.mongodb.client.gridfs.GridFSBucket;
import com.rui.api.BaseController;
import com.rui.api.controller.article.ArticleControllerApi;
import com.rui.article.service.ArticleService;
import com.rui.enums.ArticleCoverType;
import com.rui.enums.ArticleReviewStatus;
import com.rui.enums.YesOrNo;
import com.rui.exception.GraceException;
import com.rui.grace.result.GraceJSONResult;
import com.rui.grace.result.ResponseStatusEnum;
import com.rui.pojo.Category;
import com.rui.pojo.bo.NewArticleBO;
import com.rui.pojo.vo.ArticleDetailVO;
import com.rui.utils.JsonUtils;
import com.rui.utils.PagedGridResult;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/13 13:09
 * @Version 1.0
 */

@RestController
public class ArticleController extends BaseController implements ArticleControllerApi {

    final static Logger logger = LoggerFactory.getLogger(ArticleController.class);

    @Autowired
    private ArticleService articleService;

    @Autowired
    private GridFSBucket gridFSBucket;


    @Override
    public GraceJSONResult createArticle(NewArticleBO newArticleBO,
                                         BindingResult result) {

        //0.判断BindingResult中是否保存了验证失败的错误信息，如果有，说明前端的输入是有问题的(用户名或者密码，至少有一个没输入)；
        // 那么，我们就获取这个错误信息，并构建一个GraceJSONResult统一返回对象，返回；
        if (result.hasErrors()) {
            Map<String, String> map = getErrorsFromBindingResult(result);
            return GraceJSONResult.errorMap(map);
        }

        /**
         * 1.判断"文章封面类型"(即NewArticleBO类的articleType字段):
         *      如果文章是有封面的，那么NewArticleBO类的articleCover属性不能为空；
         *      如果文章是没有封面的，那么NewArticleBO类的articleCover属性设置为空；
         */
        //如果文章类型是图文的(即有一张封面)，那么"封面图片的地址，是一定要有的"，即articleCover属性不能为空
        if (newArticleBO.getArticleType() == ArticleCoverType.ONE_IMAGE.type) {
            //此时，如果articleCover属性为空了，直接返回一个信息是"文章封面不存在，请选择一个！"的GraceJSONResult;
            if (StringUtils.isBlank(newArticleBO.getArticleCover())) {
                return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_COVER_NOT_EXIST_ERROR);
            }
        }
        //如果文章类型是纯文字的，那么前端articleCover属性是不是为空，无所谓；我们直接把其设置为空即可；
        else if (newArticleBO.getArticleType() == ArticleCoverType.WORDS.type) {
            newArticleBO.setArticleCover("");
        }

        /**
         * 2.判断"前端传过来的文章分类"需要在"数据库category表"中；
         *      做法1：根据前端传过来categoryId，直接去数据库的category表中查，看有没有；（但是，
         *          由于【发表文章，接口】是提供给用户的，自然其并发量会是很大的；所以，在这个接口中，
         *          每次都去查询数据库，其实是不太好的；）
         *      做法2：根据前端传过来categoryId，redis中查；（我们在开发【查看文章领域，接口】的时
         *          候，把文章分类数据存到了缓存中；）这儿，我们采用做法2；
         */
        //从redis中，获取所有文章分类的JSON字符串
        String allCatJSON= redis.get(REDIS_ALL_CATEGORY);
        //
        /**
         * 如果redis中没有文章分类数据，那么说明系统出了一些相应的问题；我们这儿就可以返回一些提示信息；比如这儿
         * 我们可以返回一个信息是"操作失败，请重试或联系管理员"的GraceJSONResult;（PS：这种情况出现的概率，极低）
         */
        Category temp = null;
        if (StringUtils.isBlank(allCatJSON)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        } else {
            //调用JsonUtils工具类，把JSON转成List<Category>;
            List<Category> catList = JsonUtils.jsonToList(allCatJSON, Category.class);
            for (Category c : catList) {
                if (c.getId() == newArticleBO.getCategoryId()) {
                    temp = c;
                    break;//如果找到了，就把找到的赋值给temp，并直接终止循环
                }
            }
            //如果，至此temp还是为空的话，就说明"前端传过来的文章分类"是不合法的；
            if (temp == null) {
                //返回一个信息是"请选择正确的文章领域！"的GraceJSONResult;
                return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_CATEGORY_NOT_EXIST_ERROR);
            }
        }

        // 3. 调用Service层的方法，去新增文章；
        articleService.createArticle(newArticleBO, temp);

        return GraceJSONResult.ok();


    }

    @Override
    public GraceJSONResult queryMyList(String userId,
                                       String keyword,
                                       Integer status,
                                       Date startDate,
                                       Date endDate,
                                       Integer page,
                                       Integer pageSize) {

        // 1.如果前端传的userId为空； 就返回一个信息是"文章列表查询参数错误！"的GraceJSONResult;
        if (StringUtils.isBlank(userId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_QUERY_PARAMS_ERROR);
        }

        // 2.如果前端传的page或者pageSize为空，我们就给其设置默认值，page设为1，pageSize设为10；
        if (page == null) {
            page = COMMON_START_PAGE; //在BaseController中定义的常量；
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        // 3.调用Service层逻辑，去查询文章列表；
        PagedGridResult pagedGridResult = articleService.queryMyArticleList(userId, keyword, status, startDate, endDate, page, pageSize);

        // 4.把"根据前端要求，包装好的pagedGridResult对象"，返回给前端；
        return GraceJSONResult.ok(pagedGridResult);
    }


    /**
     * 后台的，根据条件，管理员查询所有用户的，文章列表，接口；
     * @param status：一个查询条件：文章的状态；（可以为空）
     * @param page：分页查询，当前页码；
     * @param pageSize：每页条目数
     * @return
     */
    @Override
    public GraceJSONResult queryAllList(Integer status, Integer page, Integer pageSize) {
        // 1.如果前端传的page或者pageSize为空，我们就给其设置默认值，page设为1，pageSize设为10；
        if (page == null) {
            page = COMMON_START_PAGE; //在BaseController中定义的常量；
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        // 3.调用Service层逻辑，去查询文章列表；
        PagedGridResult pagedGridResult = articleService.queryAllArticleListAdmin(status,page,pageSize
        );

        // 4.把"根据前端要求，包装好的pagedGridResult对象"，返回给前端；
        return GraceJSONResult.ok(pagedGridResult);
    }

    @Override
    public GraceJSONResult doReview(String articleId, Integer passOrNot) {

        Integer pendingStatus;//定义一个变量，后面用来承接，文章人工审核后，真正应该处于的状态；
        // 1.根据passOrNot的结果，分别去处理；
        if (passOrNot == YesOrNo.YES.type) {
            //如果passOrNot是1，表示审核通过；那么，文章状态应该是：3：审核通过（已发布）;
            pendingStatus = ArticleReviewStatus.SUCCESS.type;
        } else if (passOrNot == YesOrNo.NO.type) {
            //如果passOrNot是0，表示审核不通过；那么，文章状态应该是：4：审核未通过;
            pendingStatus = ArticleReviewStatus.FAILED.type;
        } else {
            //如果passOrNot既不是1也不是0，就返回一个信息是"文章审核出错！"的GraceJSONResult；
            return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_REVIEW_ERROR);
        }

        // 保存到数据库，更改文章的状态为审核成功或者失败
        articleService.updateArticleStatus(articleId, pendingStatus);

        if (pendingStatus == ArticleReviewStatus.SUCCESS.type){
            // 审核成功，生成文章详情页静态html
            try {
//                createArticleHTML(articleId);
                String articleMongoId = createArticleHTMLToGridFS(articleId);
                // 存储到对应的文章，进行关联保存
                articleService.updateArticleToGridFS(articleId, articleMongoId);
                // 调用消费端，执行下载html
                doDownloadArticleHTML(articleId, articleMongoId);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return GraceJSONResult.ok();
    }

    private void doDownloadArticleHTML(String articleId, String articleMongoId) {

        String url =
                "http://html.imoocnews.com:8002/article/html/download?articleId="
                        + articleId +
                        "&articleMongoId="
                        + articleMongoId;
        ResponseEntity<Integer> responseEntity = restTemplate.getForEntity(url, Integer.class);
        int status = responseEntity.getBody();
        System.out.println(status);
        if (status != HttpStatus.OK.value()) {
            GraceException.display(ResponseStatusEnum.ARTICLE_REVIEW_ERROR);
        }
    }


    @Value("${freemarker.html.article}")
    private String articlePath;

    // 文章生成HTML
    public void createArticleHTML(String articleId) throws Exception {

        Configuration cfg = new Configuration(Configuration.getVersion());
        String classpath = this.getClass().getResource("/").getPath();
        cfg.setDirectoryForTemplateLoading(new File(classpath + "templates"));

        Template template = cfg.getTemplate("detail.ftl", "utf-8");

        // 获得文章的详情数据
        ArticleDetailVO detailVO = getArticleDetail(articleId);
        Map<String, Object> map = new HashMap<>();
        map.put("articleDetail", detailVO);

        File tempDic = new File(articlePath);
        if (!tempDic.exists()) {
            tempDic.mkdirs();
        }

        articlePath = articlePath + File.separator + detailVO.getId() + ".html";

        Writer out = new FileWriter(articlePath);
        template.process(map, out);
        out.close();
    }




    // 文章生成HTML
    public String createArticleHTMLToGridFS(String articleId) throws Exception {

        Configuration cfg = new Configuration(Configuration.getVersion());
        String classpath = this.getClass().getResource("/").getPath();
        cfg.setDirectoryForTemplateLoading(new File(classpath + "templates"));

        Template template = cfg.getTemplate("detail.ftl", "utf-8");

        // 获得文章的详情数据
        ArticleDetailVO detailVO = getArticleDetail(articleId);
        Map<String, Object> map = new HashMap<>();
        map.put("articleDetail", detailVO);

        String htmlContent = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
//        System.out.println(htmlContent);

        InputStream inputStream = IOUtils.toInputStream(htmlContent);
        ObjectId fileId = gridFSBucket.uploadFromStream(detailVO.getId() + ".html",inputStream);
        return fileId.toString();
    }

    // 发起远程调用rest，获得文章详情数据
    public ArticleDetailVO getArticleDetail(String articleId) {
        String url
                = "http://www.imoocnews.com:8001/portal/article/detail?articleId=" + articleId;
        ResponseEntity<GraceJSONResult> responseEntity
                = restTemplate.getForEntity(url, GraceJSONResult.class);
        GraceJSONResult bodyResult = responseEntity.getBody();
        ArticleDetailVO detailVO = null;
        if (bodyResult.getStatus() == 200) {
            String detailJson = JsonUtils.objectToJson(bodyResult.getData());
            detailVO = JsonUtils.jsonToPojo(detailJson, ArticleDetailVO.class);
        }
        return detailVO;
    }


    @Override
    public GraceJSONResult delete(String userId, String articleId) {
        articleService.deleteArticle(userId, articleId);
        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult withdraw(String userId, String articleId) {
        articleService.withdrawArticle(userId, articleId);
        return GraceJSONResult.ok();
    }
}
