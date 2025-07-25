package com.edukit.core.member.exception;

import com.edukit.common.exception.BusinessException;
import com.edukit.common.exception.code.ErrorCode;

public class MemberException extends BusinessException {

    public MemberException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
