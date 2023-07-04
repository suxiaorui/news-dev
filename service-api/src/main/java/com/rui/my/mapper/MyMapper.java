package com.rui.my.mapper;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/4 19:29
 * @Version 1.0
 */
public interface MyMapper<T> extends Mapper<T>, MySqlMapper<T> {
}
