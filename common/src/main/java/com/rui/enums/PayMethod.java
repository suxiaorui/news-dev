package com.rui.enums;



/**
 * @Author suxiaorui
 * @Description 支付方式 枚举
 * @Date 2023/7/5 00:03
 * @Version 1.0
 */

public enum PayMethod {

	WEIXIN(1, "微信"),
	ALIPAY(2, "支付宝");

	public final Integer type;
	public final String value;

	PayMethod(Integer type, String value){
		this.type = type;
		this.value = value;
	}

}
