package com.edukit.studentrecord.controller.request;

import jakarta.validation.constraints.NotNull;

public record StudentRecordUpdateRequest(
        @NotNull(message = "생활기록부 내용은 필수입니다.")
        String description
) {
}
