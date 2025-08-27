package com.edukit.core.common.event.mail;

import com.edukit.core.common.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@ConditionalOnBean(EmailService.class)
public class EmailEventListener {

    private final EmailService emailService;

    private static final String SUBJECT_VERIFY = "[Edukit] 교사 인증을 위한 이메일입니다.";
    private static final String SUBJECT_PASSWORD = "[Edukit] 비밀번호 변경을 위한 이메일입니다.";

    @Async("emailTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTeacherVerifyEmailEvent(final TeacherVerificationEmailEvent event) {
        emailService.sendEmail(event.email(), event.memberUuid(), event.verificationCode(), SUBJECT_VERIFY);
    }

    @Async("emailTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePasswordFindEvent(final PasswordChangeEmailEvent event) {
        emailService.sendEmail(event.email(), event.memberUuid(), event.verificationCode(), SUBJECT_PASSWORD);
    }
}
