package com.campus.tlias.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器。
 *
 * <p>对比项目二：那边每个 Dao 方法都要写 try-catch-e.printStackTrace()。
 * 这里写一次，所有 Controller 出错都自动被接住并转成统一格式。
 *
 * <p>@RestControllerAdvice = @ControllerAdvice + @ResponseBody
 * 意思是"给所有 Controller 兜底，返回结果直接序列化成 JSON"。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 参数校验失败（@Valid 校验不通过）。
     * 例：POST /student/add 时 name 传了空字符串。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return Result.error(400, "参数校验失败：" + msg);
    }

    /**
     * 业务异常：Service 层主动抛出的（如"姓名已存在"）。
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusiness(BusinessException e) {
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 请求的接口不存在（404）。
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<Void> handleNotFound(NoHandlerFoundException e) {
        return Result.error(404, "接口不存在：" + e.getRequestURL());
    }

    /**
     * 兜底：所有没被上面捕获的异常都走这里。
     * 没有它的话，Spring Boot 默认返回一段 HTML 错误页，前端拿到 JSON 会解析失败。
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        System.err.println("[全局异常] " + e.getMessage());
        e.printStackTrace();
        return Result.error(500, "服务器内部错误：" + e.getMessage());
    }
}
