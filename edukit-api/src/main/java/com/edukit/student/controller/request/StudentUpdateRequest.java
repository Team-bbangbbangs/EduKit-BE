package com.edukit.student.controller.request;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record StudentUpdateRequest(
        @NotNull(message = "학년은 필수 입력 항목입니다.")
        Integer grade,
        @NotNull(message = "반은 필수 입력 항목입니다.")
        Integer classNumber,
        @NotNull(message = "번호는 필수 입력 항목입니다.")
        Integer studentNumber,
        @NotBlank(message = "이름은 필수 입력 항목입니다.")
        String studentName,
        @JsonSetter(nulls = Nulls.AS_EMPTY)
        List<String> recordTypes
) {
}
