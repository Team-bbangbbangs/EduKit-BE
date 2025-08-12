package com.edukit.member.controller.request;

import jakarta.validation.constraints.NotNull;

public record PasswordChangeRequest(
        @NotNull(message = "현재 비밀번호를 입력해주세요.")
        String currentPassword,
        @NotNull(message = "새로운 비밀번호를 입력해주세요.")
        String newPassword,
        @NotNull(message = "비밀번호 확인을 입력해주세요.")
        String confirmPassword
) {
}

