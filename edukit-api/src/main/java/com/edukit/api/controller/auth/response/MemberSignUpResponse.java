package com.edukit.api.controller.auth.response;

public record MemberSignUpResponse(
        String accessToken,
        boolean isAdmin
) {
    private static final boolean NOT_ADMIN = false;

    public static MemberSignUpResponse of(final String accessToken) {
        return new MemberSignUpResponse(accessToken, NOT_ADMIN);
    }
}
