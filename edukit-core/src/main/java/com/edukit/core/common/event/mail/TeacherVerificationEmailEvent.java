package com.edukit.core.common.event.mail;

public interface TeacherVerificationEmailEvent {
    String email();

    String memberUuid();

    String verificationCode();
}
