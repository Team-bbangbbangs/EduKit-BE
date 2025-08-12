package com.edukit.core.auth.event;

public record EmailSendEvent(
        String email,
        String memberUuid,
        String verificationCode
) implements VerificationEmailEvent {
    public static EmailSendEvent of(final String email, final String memberUuid, final String verificationCode) {
        return new EmailSendEvent(email, memberUuid, verificationCode);
    }
}
