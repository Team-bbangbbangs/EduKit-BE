package com.edukit.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {
                "com.edukit.batch",
                "com.edukit.core",
                "com.edukit.common",
                "com.edukit.external.aws.mail", "com.edukit.external.config", "com.edukit.external.slack"
        }
)
public class EdukitBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(EdukitBatchApplication.class, args);
    }

}
