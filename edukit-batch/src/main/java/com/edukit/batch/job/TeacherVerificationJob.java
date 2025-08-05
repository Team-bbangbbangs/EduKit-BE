package com.edukit.batch.job;

import com.edukit.core.auth.service.dto.MemberVerificationData;
import com.edukit.batch.facade.MemberBatchFacade;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TeacherVerificationJob {

    private final MemberBatchFacade memberBatchFacade;

    public void execute() {
        log.info("=== 교사 인증 배치 작업 시작 ===");
        
        try {
            log.info("1단계: 교사 인증 데이터 초기화 중...");
            List<MemberVerificationData> memberVerificationData = memberBatchFacade.initializeTeacherVerification();
            log.info("교사 인증 대상자 수: {}", memberVerificationData != null ? memberVerificationData.size() : 0);
            
            if (memberVerificationData == null || memberVerificationData.isEmpty()) {
                log.warn("교사 인증 대상자가 없습니다. 배치 작업을 종료합니다.");
                return;
            }
            
            log.info("2단계: 인증 이메일 전송 중...");
            memberBatchFacade.sendVerificationEmails(memberVerificationData);
            log.info("인증 이메일 전송 완료");
            
            log.info("=== 교사 인증 배치 작업 완료 ===");
            
        } catch (Exception e) {
            log.error("=== 교사 인증 배치 작업 실패 ===");
            log.error("작업 실패 원인: {}", e.getMessage());
            log.error("작업 실패 스택트레이스:", e);
            throw e;
        }
    }
}
