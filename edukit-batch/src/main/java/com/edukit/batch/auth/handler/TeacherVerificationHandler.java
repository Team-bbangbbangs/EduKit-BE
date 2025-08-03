package com.edukit.batch.auth.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.edukit.batch.EdukitBatchApplication;
import com.edukit.batch.auth.job.TeacherVerificationJob;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
public class TeacherVerificationHandler implements RequestHandler<Map<String, Object>, String> {

    @Override
    public String handleRequest(final Map<String, Object> input, final Context context) {
        log.info("Teacher verification batch started. RequestId: {}", context.getAwsRequestId());

        try (ConfigurableApplicationContext applicationContext =
                     SpringApplication.run(EdukitBatchApplication.class)) {

            TeacherVerificationJob job = applicationContext.getBean(TeacherVerificationJob.class);
            job.execute();

            log.info("Teacher verification batch completed successfully");
            return "SUCCESS";

        } catch (Exception e) {
            log.error("Teacher verification batch failed", e);
            throw new RuntimeException("Batch execution failed", e);
        }
    }
}
