package com.campus.tlias.common;

/**
 * 业务异常。
 *
 * <p>项目二里 Dao 层只能 return 0 表示失败，调用方无法区分
 * "数据库没连上" 还是 "姓名重复了"。
 * 用异常就能带上明确原因，由全局异常处理器统一转成 Result 返回给前端。
 */
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
