package com.edukit.common.exception;

import lombok.Getter;

@Getter
public class ExternalApiException extends RuntimeException {

    private final ErrorCode errorCode;

    public ExternalApiException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ExternalApiException(final ErrorCode errorCode, final Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}
