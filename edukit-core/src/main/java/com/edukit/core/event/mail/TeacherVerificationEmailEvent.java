package com.edukit.core.event.mail;

public interface TeacherVerificationEmailEvent {
    String email();

    String memberUuid();

    String verificationCode();
}
