package com.rui.enums;

/**
 * @Author suxiaorui
 * @Description 文章发布操作类型 枚举
 * @Date 2023/7/13 14:45
 * @Version 1.0
 */


public enum ArticleAppointType {
    TIMING(1, "文章定时发布 - 定时"),
    IMMEDIATELY(0, "文章立即发布 - 即时");

    public final Integer type;
    public final String value;

    ArticleAppointType(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}