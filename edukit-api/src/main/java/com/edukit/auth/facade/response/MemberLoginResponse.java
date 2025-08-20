package com.edukit.auth.facade.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

public record MemberLoginResponse(
        @Schema(description = "JWT 액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String accessToken,
        
        @Schema(hidden = true)
        @JsonIgnore
        String refreshToken,
        
        @Schema(description = "관리자 여부", example = "false")
        boolean isAdmin
) {
    public static MemberLoginResponse of(final String accessToken, final String refreshToken, final boolean isAdmin) {
        return new MemberLoginResponse(accessToken, refreshToken, isAdmin);
    }
}
