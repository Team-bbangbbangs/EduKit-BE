package com.edukit.external.aws.mail.exception;

import com.edukit.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MailErrorCode implements ErrorCode {

    ILLEGAL_URL_ARGUMENT("MAIL-40001", "잘못된 URL 인자입니다.");

    private final String code;
    private final String message;
}
