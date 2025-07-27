package com.edukit.api.controller.studentrecord.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StudentRecordPromptRequest(
        @NotNull(message = "필수 입력값입니다.")
        int byteCount,
        @NotBlank(message = "필수 입력값입니다.")
        String prompt
) {
}
