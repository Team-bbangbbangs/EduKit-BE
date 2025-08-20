package com.edukit.member.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record MemberEmailUpdateRequest(
        @Schema(description = "새로운 이메일 주소", example = "newteacher@sen.go.kr")
        @NotNull(message = "이메일은 필수 입력값입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email
) {
}
