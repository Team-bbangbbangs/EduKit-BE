package com.edukit.auth.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record MemberLoginRequest(
        @Schema(description = "사용자 이메일", example = "teacher@sen.go.kr")
        @NotNull(message = "이메일은 필수 입력값입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,
        
        @Schema(description = "사용자 비밀번호", example = "password123!")
        @NotNull(message = "비밀번호는 필수 입력값입니다.")
        String password
) {
}
