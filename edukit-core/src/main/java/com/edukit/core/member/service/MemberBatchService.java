package com.edukit.core.member.service;

import com.edukit.core.member.entity.Member;
import com.edukit.core.member.enums.MemberRole;
import com.edukit.core.member.repository.MemberRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberBatchService {

    private final MemberRepository memberRepository;

    @Transactional
    public void resetToTeacherVerificationStatus() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(14);
        List<Member> targetTeachers = memberRepository.findTeachersForVerificationReset(MemberRole.TEACHER, cutoffDate);

        int resetCount = 0;
        for (Member member : targetTeachers) {
            try {
                member.resetToTeacherVerificationPending();
                resetCount++;
                log.debug("Reset verification status for member: {}", member.getId());
            } catch (Exception e) {
                log.error("Failed to reset verification status for member: {}", member.getId(), e);
            }
        }
        log.info("Successfully reset verification status for {} members", resetCount);
    }
}
