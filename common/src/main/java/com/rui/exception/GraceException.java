package com.rui.exception;

import com.rui.grace.result.ResponseStatusEnum;

/**
 * @Author suxiaorui
 * @Description 优雅的处理异常 统一封装
 * @Date 2023/7/4 23:03
 * @Version 1.0
 */


public class GraceException {

    public static void display(ResponseStatusEnum responseStatusEnum){
        throw new MyCustomException(responseStatusEnum);
    }
}
