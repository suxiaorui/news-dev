package com.rui.enums;


/**
 * @Author suxiaorui
 * @Description 人脸比对类型 枚举
 * @Date 2023/7/5 00:02
 * @Version 1.0
 */

public enum FaceVerifyType {
    BASE64(1, "图片Base64对比"),
    IMAGE_URL(0, "URL图片地址对比");

    public final Integer type;
    public final String value;

    FaceVerifyType(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
