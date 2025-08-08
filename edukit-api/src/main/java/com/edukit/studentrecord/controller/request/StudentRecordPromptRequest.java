package com.edukit.studentrecord.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record StudentRecordPromptRequest(
        @Positive(message = "바이트 수는 양수여야 합니다.")
        int byteCount,
        @NotBlank(message = "필수 입력값입니다.")
        String prompt
) {
}
