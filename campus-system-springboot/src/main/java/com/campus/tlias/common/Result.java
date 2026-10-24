package com.campus.tlias.common;

/**
 * 统一响应结果封装。
 *
 * <p>为什么要有它：项目二里没有 HTTP 接口，直接打印结果。
 * 有了接口之后，客户端需要能稳定地解析"成功还是失败、成功时数据在哪"。
 * 于是约定一个固定结构：{ code, msg, data }，所有接口都返回这个结构。
 *
 * <p>这在企业项目里是硬性规范（面试常问：为什么要统一响应体？）。
 */
public class Result<T> {

    /** 200 成功，500 服务器错误，400 参数错误 */
    private Integer code;
    private String msg;
    private T data;

    public Result() {
    }

    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /** 成功且无数据 */
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }

    /** 成功且带数据 */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    /** 失败 */
    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
