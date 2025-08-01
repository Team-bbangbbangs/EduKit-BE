package com.edukit.batch.auth.job;

import com.edukit.core.member.facade.MemberBatchFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TeacherVerificationJob {

    private final MemberBatchFacade memberBatchFacade;

    public void execute() {
        memberBatchFacade.initializeTeacherVerification();
    }
}
