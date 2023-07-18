package com.rui.article;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/9 22:34
 * @Version 1.0
 */


@Component
public class GridFSConfig {

    @Value("${spring.data.mongodb.database}")
    private String mongodb;

    @Bean
    public GridFSBucket gridFSBucket(MongoClient mongoClient){
        //通过MongoClient，获得MongoDatabase;参数是，MongoDB中的某个database；
        MongoDatabase mongoDatabase = mongoClient.getDatabase(mongodb);
        //通过获得MongoDatabase，获得GridFSBucket;
        GridFSBucket bucket = GridFSBuckets.create(mongoDatabase);
        //返回获得的GridFSBucket；也就是把这个GridFSBucket实例化进IoC容器
        return bucket;
    }

}
