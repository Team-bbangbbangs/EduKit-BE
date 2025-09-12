package com.edukit.student.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record StudentDeleteRequest(
        @Schema(description = "삭제할 학생 ID 목록", example = "[1, 2, 3]")
        @NotEmpty(message = "삭제할 학생 id는 하나 이상 입력해야 합니다.")
        List<Long> studentIds
) {
}
