package com.rui.api.service;

import com.github.pagehelper.PageInfo;
import com.rui.utils.PagedGridResult;
import com.rui.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/11 21:20
 * @Version 1.0
 */


public class BaseService {

    public static final String REDIS_ALL_CATEGORY = "redis_all_category";

    @Autowired
    public RedisOperator redis;

    public PagedGridResult setterPagedGrid(List<?> list,
                                           Integer page) {
        PageInfo<?> pageList = new PageInfo<>(list);
        PagedGridResult gridResult = new PagedGridResult();
        gridResult.setRows(list);
        gridResult.setPage(page);
        gridResult.setRecords(pageList.getTotal());
        gridResult.setTotal(pageList.getPages());
        return gridResult;
    }

}

