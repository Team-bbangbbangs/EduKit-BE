package com.edukit.external.aws.sqs.exception;

import com.edukit.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SQSErrorCode implements ErrorCode {
    MESSAGE_SEND_FAIL("SQS-50001", "SQS 메시지 전송 실패"),
    MESSAGE_SERIALIZATION_FAILED("SQS-40002", "SQS 메시지 직렬화 실패"),
    MESSAGE_SIZE_EXCEEDED("SQS-40003", "SQS 메시지 크기 초과 (256KB 제한)"),
    ;

    private final String code;
    private final String message;
}
