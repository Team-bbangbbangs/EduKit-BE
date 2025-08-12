package com.edukit.core.common.event;

public interface TeacherVerificationEmailEvent {
    String email();

    String memberUuid();

    String verificationCode();
}
