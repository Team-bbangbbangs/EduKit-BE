package com.edukit.core.subject.exception;

import com.edukit.common.exception.BusinessException;

public class SubjectException extends BusinessException {

    public SubjectException(final SubjectErrorCode errorCode) {
        super(errorCode);
    }
}
