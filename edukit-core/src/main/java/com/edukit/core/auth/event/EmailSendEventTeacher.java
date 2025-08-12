package com.edukit.core.auth.event;

import com.edukit.core.common.event.TeacherVerificationEmailEvent;

public record EmailSendEventTeacher(
        String email,
        String memberUuid,
        String verificationCode
) implements TeacherVerificationEmailEvent {
    public static EmailSendEventTeacher of(final String email, final String memberUuid, final String verificationCode) {
        return new EmailSendEventTeacher(email, memberUuid, verificationCode);
    }
}
