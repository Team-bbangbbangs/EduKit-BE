package com.edukit.member.event;

import com.edukit.core.event.mail.TeacherVerificationEmailEvent;

public record MemberEmailUpdateEvent(
        String email,
        String memberUuid,
        String verificationCode
) implements TeacherVerificationEmailEvent {

    public static MemberEmailUpdateEvent of(final String email, final String memberUuid, final String verificationCode) {
        return new MemberEmailUpdateEvent(email, memberUuid, verificationCode);
    }
}
