package com.edukit.core.event.mail;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmailTemplate {

    TEACHER_VERIFY("[Edukit] 교사 인증을 위한 이메일입니다.", "email-verification"),
    PASSWORD_CHANGE("[Edukit] 비밀번호 변경을 위한 이메일입니다.", "password-change");

    private final String subject;
    private final String templateKey;
}
