package com.edukit.studentrecord.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record StudentRecordUpdateRequest(
        @Schema(description = "생활기록부 내용", example = "수학 수업에 적극적으로 참여하며, 어려운 문제도 포기하지 않고 끝까지 노력하는 모습을 보인다.")
        @NotNull(message = "생활기록부 내용은 필수입니다.")
        String description
) {
}
