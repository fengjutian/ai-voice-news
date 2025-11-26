package com.voice.news.app.exception;

import com.voice.news.app.common.R;


import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ⚠️ 处理业务异常
    @ExceptionHandler(ServiceException.class)
    public R<?> handleServiceException(ServiceException e){
        return R.error(e.getCode(), e.getMessage());
    }

    // ⚠️ 处理参数校验异常（@Valid）
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<?> handleValidException(MethodArgumentNotValidException e){
        String msg = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return R.error(ErrorCode.PARAM_ERROR.code, msg);
    }

    // ⚠️ 处理其他异常
    @ExceptionHandler(Exception.class)
    public R<?> handleException(Exception e){
        return R.error(ErrorCode.SERVER_ERROR.code, e.getMessage());
    }
}

