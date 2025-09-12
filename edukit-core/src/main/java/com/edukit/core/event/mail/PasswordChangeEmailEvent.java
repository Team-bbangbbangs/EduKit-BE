package com.edukit.core.event.mail;

public interface PasswordChangeEmailEvent {
    String email();

    String memberUuid();

    String verificationCode();
}
