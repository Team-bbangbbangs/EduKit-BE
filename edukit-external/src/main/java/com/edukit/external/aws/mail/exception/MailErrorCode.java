package com.edukit.external.aws.mail.exception;

import com.edukit.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MailErrorCode implements ErrorCode {

    ILLEGAL_URL_ARGUMENT("MAIL-40001", "잘못된 URL 인자입니다."),
    EMAIL_SEND_FAILED("MAIL-50202", "이메일 발송에 실패했습니다."),
    EMAIL_TIMEOUT("MAIL-50303", "이메일 발송 중 타임아웃이 발생했습니다."),
    EMAIL_SDK_RETRY_EXHAUSTED("MAIL-50304", "이메일 발송 재시도가 모두 실패했습니다.");

    private final String code;
    private final String message;
}
