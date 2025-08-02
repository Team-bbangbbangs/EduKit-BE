package com.edukit.core.common.handler;

import com.edukit.core.auth.event.MemberSignedUpEvent;
import com.edukit.core.member.event.MemberStatusInitializeEvent;
import com.edukit.external.aws.mail.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailEventListener {

    private final EmailService emailService;

    @Async("emailTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEmailEvent(final MemberSignedUpEvent event) {
        try {
            emailService.sendEmail(event.email(), event.memberUuid(), event.verificationCode());
        } catch (Exception e) {
            log.error("[회원가입] 이메일 발송 실패. event={} message={}", event, e.getMessage());
        }
    }

    @Async("emailTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEmailEvent(final MemberStatusInitializeEvent event) {
        try {
            for (var memberData : event.memberVerificationData()) {
                emailService.sendEmail(memberData.email(), memberData.memberUuid(), memberData.verificationCode());
            }
        } catch (Exception e) {
            log.error("[Batch 작업] 이메일 발송 실패. event={} message={}", event, e.getMessage());
        }
    }
}
