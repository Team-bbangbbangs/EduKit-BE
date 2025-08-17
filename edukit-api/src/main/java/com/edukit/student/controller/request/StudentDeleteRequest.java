package com.edukit.student.controller.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record StudentDeleteRequest(
        @NotEmpty(message = "삭제할 학생들의 정보는 필수 항목입니다.")
        List<Long> studentIds
) {
}
