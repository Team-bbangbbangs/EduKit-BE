package com.edukit.external.openai.exception;

import com.edukit.common.exception.ExternalApiException;
import com.edukit.external.openai.exception.code.OpenAiErrorCode;

public class TimeOutException extends ExternalApiException {

    public TimeOutException(final OpenAiErrorCode errorCode) {
        super(errorCode);
    }
}
