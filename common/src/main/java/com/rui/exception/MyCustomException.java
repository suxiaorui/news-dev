package com.rui.exception;

import com.rui.grace.result.ResponseStatusEnum;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/4 23:04
 * @Version 1.0
 */
public class MyCustomException extends RuntimeException {

    private ResponseStatusEnum responseStatusEnum;

    public MyCustomException(ResponseStatusEnum responseStatusEnum){
        super("异常状态码：" + responseStatusEnum.status()
                + "; 具体异常信息为" + responseStatusEnum.msg());
        this.responseStatusEnum = responseStatusEnum;
    }


    public ResponseStatusEnum getResponseStatusEnum() {
        return responseStatusEnum;
    }

    public void setResponseStatusEnum(ResponseStatusEnum responseStatusEnum) {
        this.responseStatusEnum = responseStatusEnum;
    }
}
