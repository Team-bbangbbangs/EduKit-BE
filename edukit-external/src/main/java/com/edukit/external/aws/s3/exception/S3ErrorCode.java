package com.edukit.external.aws.s3.exception;

import com.edukit.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum S3ErrorCode implements ErrorCode {
    INVALID_FILE_NAME("S3-40001", "잘못된 파일 이름입니다."),
    INVALID_FILE_EXTENSION("S3-40002", "지원하지 않는 파일 확장자입니다."),
    INVALID_FILE_URL("S3-40003", "잘못된 파일 URL입니다."),
    FILE_DELETE_FAILED("S3-50004", "파일 삭제 중 오류가 발생했습니다."),
    FILE_COPY_FAILED("S3-50005", "파일 복사 중 오류가 발생했습니다.")
    ;

    private final String code;
    private final String message;
}
