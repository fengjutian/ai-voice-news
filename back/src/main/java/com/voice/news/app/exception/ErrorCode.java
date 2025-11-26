package com.voice.news.app.exception;

public enum ErrorCode {

    SUCCESS(0, "success"),
    PARAM_ERROR(400, "请求参数不合法"),
    NOT_FOUND(404, "资源未找到"),
    UNAUTHORIZED(401, "未授权访问"),
    FORBIDDEN(403, "无权限访问"),
    SERVER_ERROR(500, "服务器内部错误"),
    BUSINESS_ERROR(600, "业务异常");

    public final int code;
    public final String message;

    ErrorCode(int code, String message){
        this.code = code;
        this.message = message;
    }
}

