package com.edukit.core.auth.event;

public interface TeacherVerificationEmailEvent {
    String email();

    String memberUuid();

    String verificationCode();
}
