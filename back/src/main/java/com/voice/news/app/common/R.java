package com.voice.news.app.common;

import com.voice.news.app.exception.ErrorCode;

import lombok.Data;

@Data
public class R<T> {

    private Integer code;
    private String message;
    private T data;

    public static <T> R<T> ok() {
        return build(ErrorCode.SUCCESS);
    }

    public static <T> R<T> ok(T data) {
        R<T> r = build(ErrorCode.SUCCESS);
        r.setData(data);
        return r;
    }

    public static <T> R<T> error(String message) {
        return error(ErrorCode.SERVER_ERROR.code, message);
    }

    public static <T> R<T> error(Integer code, String message) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMessage(message);
        return r;
    }

    public static <T> R<T> build(ErrorCode errorCode) {
        R<T> r = new R<>();
        r.setCode(errorCode.code);
        r.setMessage(errorCode.message);
        return r;
    }
}
