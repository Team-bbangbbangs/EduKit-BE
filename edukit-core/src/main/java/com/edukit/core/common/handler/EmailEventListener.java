package com.edukit.core.common.handler;

import com.edukit.core.auth.entity.VerificationCode;
import com.edukit.core.auth.enums.VerificationCodeType;
import com.edukit.core.auth.event.MemberSignedUpEvent;
import com.edukit.core.auth.service.VerificationCodeService;
import com.edukit.core.member.entity.Member;
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
    private final VerificationCodeService verificationCodeService;

    @Async("emailTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEmailEvent(final MemberSignedUpEvent event) {
        try {
            emailService.sendEmail(event.email(), event.memberUuid(), event.verificationCode());
        } catch (Exception e) {
            log.error("이메일 발송 실패. event={} message={}", event, e.getMessage());
        }
    }

    @Async("emailTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEmailEvent(final MemberStatusInitializeEvent event) {
        try {
            for (Member member : event.members()) {
                VerificationCode verificationCode = verificationCodeService.getVerificationCode(member,
                        VerificationCodeType.TEACHER_VERIFICATION);
                emailService.sendEmail(member.getEmail(), member.getMemberUuid(),
                        verificationCode.getVerificationCode());
            }
        } catch (Exception e) {
            log.error("이메일 발송 실패. event={} message={}", event, e.getMessage());
        }
    }
}
