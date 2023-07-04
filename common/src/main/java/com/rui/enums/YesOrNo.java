package com.rui.enums;


/**
 * @Author suxiaorui
 * @Description 是否 枚举
 * @Date 2023/7/5 00:02
 * @Version 1.0
 */


public enum YesOrNo {
    NO(0, "否"),
    YES(1, "是");

    public final Integer type;
    public final String value;

    YesOrNo(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
