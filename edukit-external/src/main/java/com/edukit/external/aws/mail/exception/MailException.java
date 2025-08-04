package com.edukit.external.aws.mail.exception;

import com.edukit.common.exception.ExternalApiException;

public class MailException extends ExternalApiException {

    public MailException(final MailErrorCode errorCode) {
        super(errorCode);
    }

    public MailException(final MailErrorCode errorCode, final Throwable cause) {
        super(errorCode, cause);
    }
}
