package com.edukit.studentrecord.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record StudentRecordPromptRequest(
        @Schema(description = "생성할 생활기록부의 바이트 수", example = "500", minimum = "1")
        @Positive(message = "바이트 수는 양수여야 합니다.")
        int byteCount,
        
        @Schema(description = "AI 생활기록부 생성 프롬프트", example = "학생의 수학 수업 참여도와 문제해결 능력에 대한 생활기록부를 작성해주세요.")
        @NotBlank(message = "필수 입력값입니다.")
        String prompt
) {
}
