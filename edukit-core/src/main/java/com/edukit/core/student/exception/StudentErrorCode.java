package com.edukit.core.student.exception;

import com.edukit.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StudentErrorCode implements ErrorCode {

    STUDENT_NOT_FOUND("ST-40401", "해당 학생이 존재하지 않습니다.");

    private final String code;
    private final String message;
}
