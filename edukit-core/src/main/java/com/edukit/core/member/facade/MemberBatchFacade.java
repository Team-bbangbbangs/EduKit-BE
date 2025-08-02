package com.edukit.core.member.facade;

import com.edukit.core.auth.service.VerificationCodeService;
import com.edukit.core.auth.service.dto.MemberVerificationData;
import com.edukit.core.member.entity.Member;
import com.edukit.core.member.event.MemberStatusInitializeEvent;
import com.edukit.core.member.service.MemberBatchService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class MemberBatchFacade {

    private final MemberBatchService memberBatchService;
    private final VerificationCodeService verificationCodeService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void initializeTeacherVerification() {
        memberBatchService.resetToTeacherVerificationStatus();
        List<Member> members = memberBatchService.getMembersForVerificationEmail();
        List<MemberVerificationData> memberVerificationData = verificationCodeService.issueVerificationCodesForMembers(members);
        eventPublisher.publishEvent(MemberStatusInitializeEvent.of(memberVerificationData));
    }
}
