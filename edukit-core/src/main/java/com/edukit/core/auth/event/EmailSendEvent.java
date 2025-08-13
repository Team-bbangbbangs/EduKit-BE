package com.edukit.core.auth.event;

import com.edukit.core.common.event.TeacherVerificationEmailEvent;

public record EmailSendEvent(
        String email,
        String memberUuid,
        String verificationCode
) implements TeacherVerificationEmailEvent {
    public static EmailSendEvent of(final String email, final String memberUuid, final String verificationCode) {
        return new EmailSendEvent(email, memberUuid, verificationCode);
    }
}
