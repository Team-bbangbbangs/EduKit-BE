package com.edukit.auth.facade.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

public record MemberSignUpResponse(
        @Schema(description = "JWT 액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String accessToken,
        
        @Schema(hidden = true)
        @JsonIgnore
        String refreshToken,
        
        @Schema(description = "관리자 여부 (회원가입 시 항상 false)", example = "false")
        boolean isAdmin
) {
    private static final boolean NOT_ADMIN = false;

    public static MemberSignUpResponse of(final String accessToken, final String refreshToken) {
        return new MemberSignUpResponse(accessToken, refreshToken, NOT_ADMIN);
    }
}
