package com.edukit.core.member.facade;

import com.edukit.core.auth.service.VerificationCodeService;
import com.edukit.core.member.service.MemberBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class MemberBatchFacade {

    private final MemberBatchService memberBatchService;
    private final VerificationCodeService verificationCodeService;

    @Transactional
    public void initializeTeacherVerification() {
        memberBatchService.resetToTeacherVerificationStatus();

    }
}
