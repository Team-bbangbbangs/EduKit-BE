package com.edukit.core.member.facade;

import com.edukit.core.auth.service.VerificationCodeService;
import com.edukit.core.auth.service.dto.MemberVerificationData;
import com.edukit.core.member.db.entity.Member;
import com.edukit.core.member.service.MemberBatchService;
import com.edukit.external.aws.mail.EmailService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberBatchFacade {

    private final EmailService emailService;
    private final MemberBatchService memberBatchService;
    private final VerificationCodeService verificationCodeService;

    @Transactional
    public List<MemberVerificationData> initializeTeacherVerification() {
        memberBatchService.resetToTeacherVerificationStatus();
        List<Member> members = memberBatchService.getMembersForVerificationEmail();
        return verificationCodeService.issueVerificationCodesForMembers(members);
    }

    public void sendVerificationEmails(final List<MemberVerificationData> memberVerificationData) {
        for (MemberVerificationData data : memberVerificationData) {
            try {
                emailService.sendEmail(data.email(), data.memberUuid(), data.verificationCode());
            } catch (Exception e) {
                log.error("[회원 배치] 이메일 발송 실패. email={} memberUuid={} message={}",
                        data.email(), data.memberUuid(), e.getMessage());
            }
        }
    }
}
