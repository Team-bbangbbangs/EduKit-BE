package com.edukit.student.controller.request;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record StudentCreateRequest(
        @Schema(description = "학생 학년", example = "2", minimum = "1", maximum = "3")
        @NotNull(message = "학년은 필수 입력 항목입니다.")
        Integer grade,
        
        @Schema(description = "학생 반 번호", example = "5", minimum = "1")
        @NotNull(message = "반은 필수 입력 항목입니다.")
        Integer classNumber,
        
        @Schema(description = "학생 번호", example = "15", minimum = "1")
        @NotNull(message = "번호는 필수 입력 항목입니다.")
        Integer studentNumber,
        
        @Schema(description = "학생 이름", example = "홍길동")
        @NotBlank(message = "이름은 필수 입력 항목입니다.")
        String studentName,
        
        @Schema(description = "생활기록부 유형 목록", example = "[\"BEHAVIOR\", \"SUBJECT\", \"CAREER\"]", allowableValues = {"BEHAVIOR", "CAREER", "CLUB", "FREE", "SUBJECT"})
        @JsonSetter(nulls = Nulls.AS_EMPTY)
        List<String> recordTypes
) {
}
