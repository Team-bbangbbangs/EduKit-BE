package com.edukit.batch.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.edukit.batch.job.TeacherVerificationJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;
import com.edukit.batch.EdukitBatchApplication;

@Slf4j
public class TeacherVerificationLambdaHandler implements RequestHandler<ScheduledEvent, String> {

    private ConfigurableApplicationContext applicationContext;

    @Override
    public String handleRequest(ScheduledEvent event, Context context) {
        // Lambda 컨텍스트 정보 로깅
        log.info("=== Lambda 실행 시작 ===");
        log.info("Request ID: {}", context.getAwsRequestId());
        log.info("Function Name: {}", context.getFunctionName());
        log.info("Function Version: {}", context.getFunctionVersion());
        log.info("Remaining Time: {}ms", context.getRemainingTimeInMillis());
        log.info("Event Time: {}", event != null ? event.getTime() : "null");
        log.info("Active Profiles: {}", System.getProperty("spring.profiles.active", "없음"));
        
        try {
            // Spring Boot 컨텍스트 초기화 (한 번만)
            if (applicationContext == null) {
                log.info("Spring 컨텍스트 초기화 중...");
                initializeSpringContext();
                log.info("Spring 컨텍스트 초기화 완료");
            } else {
                log.info("기존 Spring 컨텍스트 재사용");
            }
            
            // 배치 작업 실행
            log.info("배치 작업 실행 중...");
            TeacherVerificationJob job = applicationContext.getBean(TeacherVerificationJob.class);
            job.execute();
            
            log.info("=== 배치 작업 성공 완료 ===");
            return "SUCCESS";
            
        } catch (Exception e) {
            log.error("=== 배치 작업 실행 실패 ===");
            log.error("오류 타입: {}", e.getClass().getSimpleName());
            log.error("오류 메시지: {}", e.getMessage());
            log.error("Lambda 남은 시간: {}ms", context.getRemainingTimeInMillis());
            log.error("상세 스택트레이스:", e);
            
            // RuntimeException으로 래핑하여 Lambda에서 올바르게 처리되도록
            throw new RuntimeException("Batch job execution failed: " + e.getMessage(), e);
        }
    }
    
    private void initializeSpringContext() {
        log.info("=== Spring 컨텍스트 초기화 시작 ===");
        log.info("Java 버전: {}", System.getProperty("java.version"));
        
        // Lambda 환경 변수를 시스템 프로퍼티로 설정
        String springProfilesActive = System.getenv("SPRING_PROFILES_ACTIVE");
        log.info("환경 변수 SPRING_PROFILES_ACTIVE: {}", springProfilesActive);
        
        if (springProfilesActive != null && !springProfilesActive.trim().isEmpty()) {
            System.setProperty("spring.profiles.active", springProfilesActive);
            log.info("시스템 프로퍼티로 설정된 spring.profiles.active: {}", springProfilesActive);
        } else {
            // 기본값으로 dev 설정
            System.setProperty("spring.profiles.active", "dev");
            log.warn("SPRING_PROFILES_ACTIVE 환경 변수가 없어서 기본값 'dev'로 설정");
        }
        
        log.info("최종 시스템 프로퍼티 spring.profiles.active: {}", System.getProperty("spring.profiles.active"));
        
        try {
            SpringApplication app = new SpringApplication(EdukitBatchApplication.class);
            app.setWebApplicationType(WebApplicationType.NONE);
            app.setBannerMode(org.springframework.boot.Banner.Mode.OFF);
            app.setRegisterShutdownHook(false);
            app.setLazyInitialization(true); // Lambda용 최적화
            
            log.info("SpringApplication 설정 완료, 컨텍스트 시작 중...");
            this.applicationContext = app.run();
            log.info("Spring 컨텍스트 초기화 성공");
            
            // 로드된 빈 수 확인
            String[] beanNames = applicationContext.getBeanDefinitionNames();
            log.info("로드된 Spring 빈 개수: {}", beanNames.length);
            
        } catch (Exception e) {
            log.error("Spring 컨텍스트 초기화 실패");
            log.error("초기화 오류 타입: {}", e.getClass().getSimpleName());
            log.error("초기화 오류 메시지: {}", e.getMessage());
            log.error("초기화 오류 스택트레이스:", e);
            throw e;
        }
    }
}
