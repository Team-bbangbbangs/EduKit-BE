package com.edukit.student.controller.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record StudentDeleteRequest(
        @NotEmpty(message = "삭제할 학생 id는 하나 이상 입력해야 합니다.")
        List<Long> studentIds
) {
}
