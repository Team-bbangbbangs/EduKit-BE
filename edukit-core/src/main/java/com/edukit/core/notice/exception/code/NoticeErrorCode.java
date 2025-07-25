package com.edukit.core.notice.exception.code;

import com.edukit.common.exception.code.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NoticeErrorCode implements ErrorCode {

    INVALID_NOTICE_CATEGORY("NO-40001", "유효하지 않은 공지사항 카테고리입니다."),
    NOTICE_NOT_FOUND("NO-40402", "해당 공지사항이 존재하지 않습니다.")
    ;

    private final String code;
    private final String message;
}
