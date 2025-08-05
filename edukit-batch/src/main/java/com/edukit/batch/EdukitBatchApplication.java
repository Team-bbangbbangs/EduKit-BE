package com.edukit.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {
                "com.edukit.batch",
                "com.edukit.core",
                "com.edukit.common",
                "com.edukit.external.slack", "com.edukit.external.aws.mail", "com.edukit.external.config"
        }
)
public class EdukitBatchApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(EdukitBatchApplication.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.setBannerMode(org.springframework.boot.Banner.Mode.OFF);
        app.setRegisterShutdownHook(false);
        app.setLazyInitialization(true); // Lambda 최적화
        app.run(args);
    }

}
