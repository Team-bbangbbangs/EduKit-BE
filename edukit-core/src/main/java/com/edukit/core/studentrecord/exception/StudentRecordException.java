package com.edukit.core.studentrecord.exception;

import com.edukit.common.exception.BusinessException;

public class StudentRecordException extends BusinessException {

    public StudentRecordException(final StudentRecordErrorCode errorCode) {
        super(errorCode);
    }

    public StudentRecordException(final StudentRecordErrorCode errorCode, final Throwable cause) {
        super(errorCode, cause);
    }
}
