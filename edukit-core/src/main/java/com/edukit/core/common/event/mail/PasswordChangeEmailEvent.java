package com.edukit.core.common.event.mail;

public interface PasswordChangeEmailEvent {
    String email();

    String memberUuid();

    String verificationCode();
}
