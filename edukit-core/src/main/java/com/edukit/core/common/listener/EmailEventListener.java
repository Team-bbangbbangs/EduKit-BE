package com.edukit.core.common.listener;

import com.edukit.core.auth.event.MemberSignedUpEvent;
import com.edukit.core.common.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnBean(EmailService.class)
public class EmailEventListener {

    private final EmailService emailService;

    @Async("emailTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEmailEvent(final MemberSignedUpEvent event) {
        if (event.mdcContext() != null) {
            MDC.setContextMap(event.mdcContext());
        }

        try {
            log.info("[회원가입] 이메일 발송 시작. to={}", event.email());
            emailService.sendEmail(event.email(), event.memberUuid(), event.verificationCode());
            log.info("[회원가입] 이메일 발송 성공. event={}", event);
        } catch (Exception e) {
            log.error("[회원가입] 이메일 발송 실패. event={} message={}", event, e.getMessage());
        }
    }
}
