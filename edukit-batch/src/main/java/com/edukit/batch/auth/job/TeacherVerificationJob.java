package com.edukit.batch.auth.job;

import com.edukit.core.auth.service.dto.MemberVerificationData;
import com.edukit.batch.auth.facade.MemberBatchFacade;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeacherVerificationJob {

    private final MemberBatchFacade memberBatchFacade;

    public void execute() {
        List<MemberVerificationData> memberVerificationData = memberBatchFacade.initializeTeacherVerification();
        memberBatchFacade.sendVerificationEmails(memberVerificationData);
    }
}
