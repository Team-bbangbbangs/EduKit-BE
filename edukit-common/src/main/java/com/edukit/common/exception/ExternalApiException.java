package com.edukit.common.exception;

import lombok.Getter;

@Getter
public class ExternalApiException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String customMessage;

    public ExternalApiException(final ErrorCode errorCode) {
        this(errorCode, errorCode.getMessage());
    }

    public ExternalApiException(final ErrorCode errorCode, final String customMessage) {
        super(customMessage);
        this.customMessage = customMessage;
        this.errorCode = errorCode;
    }
}
