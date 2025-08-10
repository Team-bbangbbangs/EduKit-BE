package com.edukit.core.student.exception;

import com.edukit.common.exception.BusinessException;

public class StudentException extends BusinessException {

    public StudentException(final StudentErrorCode errorCode) {
        super(errorCode);
    }

    public StudentException(final StudentErrorCode errorCode, final Throwable e) {
        super(errorCode, e);
    }
}
