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
        log.info("Teacher verification batch job started at: {}", event.getTime());
        
        try {
            // Spring Boot 컨텍스트 초기화 (한 번만)
            if (applicationContext == null) {
                initializeSpringContext();
            }
            
            // 배치 작업 실행
            TeacherVerificationJob job = applicationContext.getBean(TeacherVerificationJob.class);
            job.execute();
            
            log.info("Teacher verification batch job completed successfully");
            return "SUCCESS";
            
        } catch (Exception e) {
            log.error("Teacher verification batch job failed", e);
            throw new RuntimeException("Batch job execution failed", e);
        }
    }
    
    private void initializeSpringContext() {
        log.info("Initializing Spring context for Lambda");
        SpringApplication app = new SpringApplication(EdukitBatchApplication.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.setBannerMode(org.springframework.boot.Banner.Mode.OFF);
        app.setRegisterShutdownHook(false);
        app.setLazyInitialization(true); // Lambda용 최적화
        this.applicationContext = app.run();
    }
}
