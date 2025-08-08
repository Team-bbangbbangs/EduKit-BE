package com.edukit.core.common.listener;

import com.edukit.core.auth.event.MemberSignedUpEvent;
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
    public void handleEmailEvent(final MemberSignedUpEvent event) {
        emailService.sendEmail(event.email(), event.memberUuid(), event.verificationCode());
    }
}
