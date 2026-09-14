package com.zephyr.ai.api;

import cn.dev33.satoken.exception.NotLoginException;
import com.object.common.brick.common.BaseResponse;
import com.object.common.brick.common.ErrorCode;
import com.object.common.brick.utils.ResultUtil;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Sa-Token 异常适配，将未登录响应转换为项目统一响应格式。
 */
@Order(0)
@RestControllerAdvice
public class SaTokenExceptionHandler {

    @ExceptionHandler(NotLoginException.class)
    public BaseResponse<?> notLoginExceptionHandler(NotLoginException exception) {
        return ResultUtil.error(ErrorCode.NOT_LOGIN, "请先登录");
    }
}
