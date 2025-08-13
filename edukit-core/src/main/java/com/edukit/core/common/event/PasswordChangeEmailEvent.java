package com.edukit.core.common.event;

public interface PasswordChangeEmailEvent {
    String email();

    String memberUuid();

    String verificationCode();
}
