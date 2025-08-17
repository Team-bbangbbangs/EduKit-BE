package com.edukit.external.aws.sqs.exception;

import com.edukit.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SQSErrorCode implements ErrorCode {
    MESSAGE_SEND_FAIL("SQS-50001", "SQS 메시지 전송 실패");

    private final String code;
    private final String message;
}
