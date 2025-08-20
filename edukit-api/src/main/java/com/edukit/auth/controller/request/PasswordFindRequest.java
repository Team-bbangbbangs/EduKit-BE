package com.edukit.auth.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record PasswordFindRequest(
        @Schema(description = "비밀번호를 찾을 이메일 주소", example = "teacher@sen.go.kr")
        @Email(message = "유효한 이메일 형식이 아닙니다.")
        @NotNull(message = "이메일 주소를 입력해주세요.")
        String email
) {
}
