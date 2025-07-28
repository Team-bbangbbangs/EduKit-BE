package com.edukit.api.controller.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record UpdatePasswordRequest(
        @NotNull(message = "인증 코드는 필수입니다.")
        String verificationCode,
        @Email(message = "유효한 이메일 형식이 아닙니다.")
        @NotNull(message = "이메일 주소를 입력해주세요.")
        String email,
        @NotNull(message = "새 비밀번호를 입력해주세요.")
        String password,
        @NotNull(message = "비밀번호 확인을 입력해주세요.")
        String confirmPassword
) {
}
