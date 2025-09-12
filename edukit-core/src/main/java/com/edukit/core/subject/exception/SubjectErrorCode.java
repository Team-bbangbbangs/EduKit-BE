package com.edukit.core.subject.exception;

import com.edukit.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubjectErrorCode implements ErrorCode {

    SUBJECT_NOT_FOUND("S-40401", "해당 과목이 존재하지 않습니다.");

    private final String code;
    private final String message;
}
