package com.togather.common.exception;

import lombok.Getter;

@Getter
public class TogatherApiException extends RuntimeException {

    private ErrorCode errorCode;

    public TogatherApiException(ErrorCode errorCode) {
        super();
        this.errorCode = errorCode;
    }
}
