package com.rui.api;

import com.rui.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/4 22:23
 * @Version 1.0
 */


public class BaseController {

    @Autowired
    public RedisOperator redis;

    public static final String MOBILE_SMSCODE = "mobile:smscode";
}
