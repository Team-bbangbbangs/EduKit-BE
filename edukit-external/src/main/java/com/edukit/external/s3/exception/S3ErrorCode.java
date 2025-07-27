package com.edukit.external.s3.exception;

import com.edukit.common.exception.code.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum S3ErrorCode implements ErrorCode {
    INVALID_FILE_NAME("S3-40001", "잘못된 파일 이름입니다."),
    INVALID_FILE_EXTENSION("S3-40002", "지원하지 않는 파일 확장자입니다.")
    ;

    private final String code;
    private final String message;
}
