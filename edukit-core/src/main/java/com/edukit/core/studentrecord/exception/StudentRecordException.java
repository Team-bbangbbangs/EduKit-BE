package com.edukit.core.studentrecord.exception;

import com.edukit.common.exception.BusinessException;
import com.edukit.common.exception.ErrorCode;

public class StudentRecordException extends BusinessException {
    public StudentRecordException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
