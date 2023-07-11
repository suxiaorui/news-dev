package com.rui.pojo.bo.validate;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/11 20:17
 * @Version 1.0
 */


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckUrlValidate.class)//通过CheckUrlValidate类，来具体做校验；
public @interface CheckUrl {

    String message() default "Url不正确";//如果校验出了问题，就会抛出这个默认的message;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
