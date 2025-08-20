package com.edukit.member.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record PasswordChangeRequest(
        @Schema(description = "현재 비밀번호", example = "currentPassword123!")
        @NotNull(message = "현재 비밀번호를 입력해주세요.")
        String currentPassword,
        
        @Schema(description = "새로운 비밀번호", example = "newPassword123!")
        @NotNull(message = "새로운 비밀번호를 입력해주세요.")
        String newPassword,
        
        @Schema(description = "비밀번호 확인", example = "newPassword123!")
        @NotNull(message = "비밀번호 확인을 입력해주세요.")
        String confirmPassword
) {
}

