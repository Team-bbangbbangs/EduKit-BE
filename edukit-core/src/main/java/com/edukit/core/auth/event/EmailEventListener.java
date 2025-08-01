package com.edukit.core.auth.event;

import com.edukit.external.aws.mail.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailEventListener {

    private final EmailService emailService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEmailEvent(final MemberSignedUpEvent event) {
        try {
            emailService.sendEmail(event.email(), event.memberUuid(), event.verificationCode());
        } catch (Exception e) {
            log.error("이메일 발송 실패. event={} message={}", event, e.getMessage());
        }
    }
}
