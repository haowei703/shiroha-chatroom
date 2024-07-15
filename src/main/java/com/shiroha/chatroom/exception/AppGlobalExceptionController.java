package com.shiroha.chatroom.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理控制器
 */
@RestControllerAdvice
public class AppGlobalExceptionController {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public String handleException(Exception e) {
        return e.getMessage();
    }
}
