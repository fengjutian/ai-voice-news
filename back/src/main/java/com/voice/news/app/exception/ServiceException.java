package com.voice.news.app.exception;

public class ServiceException extends RuntimeException {

    private final Integer code;

    public ServiceException(ErrorCode errorCode) {
        super(errorCode.message);
        this.code = errorCode.code;
    }

    public ServiceException(int code, String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}

