package com.edukit.auth.event;

import com.edukit.core.common.event.mail.TeacherVerificationEmailEvent;

public record MemberSignedUpEvent(
        String email,
        String memberUuid,
        String verificationCode
) implements TeacherVerificationEmailEvent {

    public static MemberSignedUpEvent of(final String email, final String memberUuid, final String verificationCode) {
        return new MemberSignedUpEvent(email, memberUuid, verificationCode);
    }
}
