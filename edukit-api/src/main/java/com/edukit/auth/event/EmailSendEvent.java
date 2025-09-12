package com.edukit.auth.event;

import com.edukit.core.event.mail.TeacherVerificationEmailEvent;

public record EmailSendEvent(
        String email,
        String memberUuid,
        String verificationCode
) implements TeacherVerificationEmailEvent {
    public static EmailSendEvent of(final String email, final String memberUuid, final String verificationCode) {
        return new EmailSendEvent(email, memberUuid, verificationCode);
    }
}
