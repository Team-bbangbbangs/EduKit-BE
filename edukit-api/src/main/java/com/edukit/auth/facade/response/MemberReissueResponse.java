package com.edukit.auth.facade.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

public record MemberReissueResponse(
        @Schema(description = "새로운 JWT 액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String accessToken,
        
        @Schema(hidden = true)
        @JsonIgnore
        String refreshToken,
        
        @Schema(description = "관리자 여부", example = "false")
        boolean isAdmin
) {
    public static MemberReissueResponse of(final String accessToken, final String refreshToken, final boolean isAdmin) {
        return new MemberReissueResponse(accessToken, refreshToken, isAdmin);
    }
}
