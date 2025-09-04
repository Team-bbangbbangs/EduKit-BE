package com.edukit.core.student.exception;

import com.edukit.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StudentErrorCode implements ErrorCode {

    STUDENT_NOT_FOUND("ST-40401", "해당 학생이 존재하지 않습니다."),
    EXCEL_FILE_READ_ERROR("ST-40002", "엑셀 파일을 읽을 수 없습니다."),
    EXCEL_FILE_FORMAT_ERROR("ST-40003", "엑셀 파일 형식이 올바르지 않습니다."),
    EXCEL_FILE_CREATE_FAIL("ST-50004", "엑셀 파일 생성 중 오류가 발생했습니다."),
    STUDENT_ALREADY_EXIST_ERROR("ST-40905", "이미 등록된 학생입니다."),
    EXCEL_RECORD_FORMAT_ERROR("ST-40006", "엑셀 레코드 형식이 올바르지 않습니다.")
    ;

    private final String code;
    private final String message;
}
