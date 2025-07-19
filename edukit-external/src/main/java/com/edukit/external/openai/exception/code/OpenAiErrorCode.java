package com.edukit.external.openai.exception.code;

import com.edukit.common.StatusCode;
import com.edukit.common.exception.code.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OpenAiErrorCode implements ErrorCode {

    OPEN_AI_TIMEOUT(StatusCode.GATEWAY_TIMEOUT.getStatus(), "AI-001", "서버 응답 시간이 초과되었습니다. 잠시 후 다시 시도해주세요.")
    ;

    private final int status;
    private final String code;
    private final String message;
}
