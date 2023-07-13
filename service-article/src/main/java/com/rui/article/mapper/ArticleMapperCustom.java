package com.rui.article.mapper;

import com.rui.my.mapper.MyMapper;
import com.rui.pojo.Article;
import org.springframework.stereotype.Repository;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/13 15:34
 * @Version 1.0
 */


@Repository
public interface ArticleMapperCustom extends MyMapper<Article> {

    public void updateAppointToPublish();

}
