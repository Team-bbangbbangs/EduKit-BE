package com.edukit.external.openai.exception;

import com.edukit.common.exception.ExternalApiException;
import com.edukit.external.openai.exception.code.OpenAiErrorCode;

public class OpenAiException extends ExternalApiException {

    public OpenAiException(final OpenAiErrorCode errorCode) {
        super(errorCode);
    }
}
