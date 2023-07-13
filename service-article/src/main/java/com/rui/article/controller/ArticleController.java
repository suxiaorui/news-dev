package com.rui.article.controller;

import com.rui.api.BaseController;
import com.rui.api.controller.article.ArticleControllerApi;
import com.rui.article.service.ArticleService;
import com.rui.enums.ArticleCoverType;
import com.rui.grace.result.GraceJSONResult;
import com.rui.grace.result.ResponseStatusEnum;
import com.rui.pojo.Category;
import com.rui.pojo.bo.NewArticleBO;
import com.rui.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

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
}
