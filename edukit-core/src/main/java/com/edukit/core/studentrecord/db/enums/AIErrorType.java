package com.edukit.core.studentrecord.db.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AIErrorType {
    OPENAI_API_ERROR("OpenAI API 호출 실패"),
    LAMBDA_ERROR("Lambda 처리 오류"),
    UNKNOWN_ERROR("알 수 없는 오류");

    private final String description;
}
