package com.rui.grace.result;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/4 18:06
 * @Version 1.0
 */


public class RuiJSONResult {

    // 响应业务状态
    private Integer status;

    // 响应消息
    private String msg;

    // 响应中的数据
    private Object data;

    private String ok;	// 不使用

    public static RuiJSONResult build(Integer status, String msg, Object data) {
        return new RuiJSONResult(status, msg, data);
    }

    public static RuiJSONResult build(Integer status, String msg, Object data, String ok) {
        return new RuiJSONResult(status, msg, data, ok);
    }

    public static RuiJSONResult ok(Object data) {
        return new RuiJSONResult(data);
    }

    public static RuiJSONResult ok() {
        return new RuiJSONResult(null);
    }

    public static RuiJSONResult errorMsg(String msg) {
        return new RuiJSONResult(500, msg, null);
    }

    public static RuiJSONResult errorUserTicket(String msg) {
        return new RuiJSONResult(557, msg, null);
    }

    public static RuiJSONResult errorMap(Object data) {
        return new RuiJSONResult(501, "error", data);
    }

    public static RuiJSONResult errorTokenMsg(String msg) {
        return new RuiJSONResult(502, msg, null);
    }

    public static RuiJSONResult errorException(String msg) {
        return new RuiJSONResult(555, msg, null);
    }

    public static RuiJSONResult errorUserQQ(String msg) {
        return new RuiJSONResult(556, msg, null);
    }

    public RuiJSONResult() {

    }

    public RuiJSONResult(Integer status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public RuiJSONResult(Integer status, String msg, Object data, String ok) {
        this.status = status;
        this.msg = msg;
        this.data = data;
        this.ok = ok;
    }

    public RuiJSONResult(Object data) {
        this.status = 200;
        this.msg = "OK";
        this.data = data;
    }

    public Boolean isOK() {
        return this.status == 200;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getOk() {
        return ok;
    }

    public void setOk(String ok) {
        this.ok = ok;
    }

}
