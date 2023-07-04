package com.rui.enums;


/**
 * @Author suxiaorui
 * @Description 性别 枚举
 * @Date 2023/7/5 00:02
 * @Version 1.0
 */

public enum Sex {
    woman(0, "女"),
    man(1, "男"),
    secret(2, "保密");

    public final Integer type;
    public final String value;

    Sex(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
