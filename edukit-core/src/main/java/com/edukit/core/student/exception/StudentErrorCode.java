package com.edukit.core.student.exception;

import com.edukit.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StudentErrorCode implements ErrorCode {

    STUDENT_NOT_FOUND("ST-40401", "해당 학생이 존재하지 않습니다."),
    EXCEL_FILE_READ_ERROR("ST-40002", "엑셀 파일을 읽을 수 없습니다."),
    EXCEL_FILE_FORMAT_ERROR("ST-40003", "엑셀 파일 형식이 올바르지 않습니다.");

    private final String code;
    private final String message;
}
