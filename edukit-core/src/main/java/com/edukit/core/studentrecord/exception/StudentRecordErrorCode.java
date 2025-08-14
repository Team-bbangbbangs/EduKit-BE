package com.edukit.core.studentrecord.exception;

import com.edukit.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StudentRecordErrorCode implements ErrorCode {

    STUDENT_RECORD_NOT_FOUND("SR-40401", "해당 학생 기록이 존재하지 않습니다."),
    PERMISSION_DENIED("SR-40302", "해당 학생 기록에 대한 권한이 없습니다."),
    STUDENT_RECORD_TYPE_NOT_FOUND("SR-40403", "유효하지 않는 생활기록부 항목입니다.")
    ;

    private final String code;
    private final String message;
}
