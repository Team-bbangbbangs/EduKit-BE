package com.edukit.core.auth.event;

public record PasswordFindEvent(
        String email,
        String memberUuid,
        String verificationCode
) {
    public static PasswordFindEvent of(final String email, final String memberUuid, final String verificationCode) {
        return new PasswordFindEvent(email, memberUuid, verificationCode);
    }
}
