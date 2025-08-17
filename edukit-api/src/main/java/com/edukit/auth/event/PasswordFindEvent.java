package com.edukit.auth.event;

import com.edukit.core.common.event.mail.PasswordChangeEmailEvent;

public record PasswordFindEvent(
        String email,
        String memberUuid,
        String verificationCode
) implements PasswordChangeEmailEvent {
    public static PasswordFindEvent of(final String email, final String memberUuid, final String verificationCode) {
        return new PasswordFindEvent(email, memberUuid, verificationCode);
    }
}
