package com.edukit.external.openai.exception;

import com.edukit.common.exception.ExternalApiException;
import com.edukit.external.openai.exception.code.OpenAiErrorCode;

public class OpenAiCallException extends ExternalApiException {

    public OpenAiCallException(final OpenAiErrorCode errorCode) {
        super(errorCode);
    }
}
