package com.edukit.external.aws.mail.exception;

import com.edukit.common.exception.ExternalException;

public class MailException extends ExternalException {

    public MailException(final MailErrorCode errorCode) {
        super(errorCode);
    }

    public MailException(final MailErrorCode errorCode, final String target) {
        super(errorCode, String.format(errorCode.getMessage(), target));
    }
}
