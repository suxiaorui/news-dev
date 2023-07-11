package com.rui.pojo.bo.validate;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/11 20:18
 * @Version 1.0
 */


import com.rui.utils.UrlUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 该类实现了ConstraintValidator这个验证器接口;
 */
public class CheckUrlValidate implements ConstraintValidator<CheckUrl, String> {

    @Override
    public boolean isValid(String url, ConstraintValidatorContext context) {
        return UrlUtil.verifyUrl(url.trim());
    }
}
