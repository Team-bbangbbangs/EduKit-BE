package com.edukit.api.controller.auth.request;

import jakarta.validation.constraints.NotNull;

public record UpdatePasswordRequest(
        @NotNull(message = "유저 uuid를 입력해주세요.")
        String memberUuid,
        @NotNull(message = "인증 코드는 필수입니다.")
        String verificationCode,
        @NotNull(message = "새 비밀번호를 입력해주세요.")
        String password,
        @NotNull(message = "비밀번호 확인을 입력해주세요.")
        String confirmPassword
) {
}
