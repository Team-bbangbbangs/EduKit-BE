package com.edukit.core.event.mail;

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

    @Async("emailTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTeacherVerifyEmailEvent(final TeacherVerificationEmailEvent event) {
        emailService.sendEmail(event.email(), event.memberUuid(), event.verificationCode(), EmailTemplate.TEACHER_VERIFY);
    }

    @Async("emailTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePasswordFindEvent(final PasswordChangeEmailEvent event) {
        emailService.sendEmail(event.email(), event.memberUuid(), event.verificationCode(), EmailTemplate.PASSWORD_CHANGE);
    }
}
