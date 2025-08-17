package com.edukit.common.exception;

import lombok.Getter;

@Getter
public class ExternalException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String customMessage;

    public ExternalException(final ErrorCode errorCode) {
        this(errorCode, errorCode.getMessage());
    }

    public ExternalException(final ErrorCode errorCode, final String customMessage) {
        super(customMessage);
        this.customMessage = customMessage;
        this.errorCode = errorCode;
    }
}
