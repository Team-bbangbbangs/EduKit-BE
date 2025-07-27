package com.edukit.core.auth.event;

public record MemberSignedUpEvent(
        String email,
        String memberUuid,
        String verificationCode
) {
    public static MemberSignedUpEvent of(final String email, final String memberUuid, final String verificationCode) {
        return new MemberSignedUpEvent(email, memberUuid, verificationCode);
    }
}
