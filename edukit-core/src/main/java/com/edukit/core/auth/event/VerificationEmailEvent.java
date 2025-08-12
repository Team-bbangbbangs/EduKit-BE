package com.edukit.core.auth.event;

public interface VerificationEmailEvent {
    String email();

    String memberUuid();

    String verificationCode();
}
