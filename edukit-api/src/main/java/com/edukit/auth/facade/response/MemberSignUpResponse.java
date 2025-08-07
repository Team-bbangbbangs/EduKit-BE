package com.edukit.auth.facade.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record MemberSignUpResponse(
        String accessToken,
        @JsonIgnore
        String refreshToken,
        boolean isAdmin
) {
    private static final boolean NOT_ADMIN = false;

    public static MemberSignUpResponse of(final String accessToken, final String refreshToken) {
        return new MemberSignUpResponse(accessToken, refreshToken, NOT_ADMIN);
    }
}
