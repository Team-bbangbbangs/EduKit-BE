package com.edukit.external.ai.exception;

import com.edukit.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OpenAiErrorCode implements ErrorCode {

    OPEN_AI_TIMEOUT("AI-50401", "서버 응답 시간이 초과되었습니다. 잠시 후 다시 시도해주세요."),
    OPEN_AI_INTERNAL_ERROR("AI-50002", "AI 호출 과정 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");

    private final String code;
    private final String message;
}
