package com.edukit.auth.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UpdatePasswordRequest(
        @Schema(description = "사용자 UUID", example = "123e4567-e89b-12d3-a456-426614174000")
        @NotNull(message = "유저 uuid를 입력해주세요.")
        String memberUuid,
        
        @Schema(description = "인증 코드", example = "123456")
        @NotNull(message = "인증 코드는 필수입니다.")
        String verificationCode,
        
        @Schema(description = "새로운 비밀번호", example = "newSecurePassword123!")
        @NotNull(message = "새 비밀번호를 입력해주세요.")
        String password,
        
        @Schema(description = "비밀번호 확인", example = "newSecurePassword123!")
        @NotNull(message = "비밀번호 확인을 입력해주세요.")
        String confirmPassword
) {
}
