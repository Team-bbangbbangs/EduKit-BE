package com.edukit.core.auth.service.dto;

public record MemberVerificationData(
        String email,
        String memberUuid,
        String verificationCode
) {
    public static MemberVerificationData of(final String email, final String memberUuid,
                                            final String verificationCode) {
        return new MemberVerificationData(email, memberUuid, verificationCode);
    }
}
