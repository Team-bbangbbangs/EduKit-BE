package com.edukit.auth.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record MemberSignUpRequest(
        @Schema(description = "사용자 이메일", example = "teacher@sen.go.kr")
        @NotNull(message = "이메일은 필수 입력값입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,
        
        @Schema(description = "사용자 비밀번호", example = "securePassword123!")
        @NotNull(message = "비밀번호는 필수 입력값입니다.")
        String password,
        
        @Schema(description = "담당 과목", example = "수학")
        @NotNull(message = "과목은 필수 입력값입니다.")
        String subject,
        
        @Schema(description = "사용자 닉네임", example = "김선생")
        @NotNull(message = "닉네임은 필수 입력값입니다.")
        String nickname,
        
        @Schema(description = "학교 종류", example = "high", allowableValues = {"middle", "high"})
        @NotNull(message = "학교는 필수 입력값입니다.")
        String school
) {
}
