package com.edukit.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {
    "com.edukit.batch",
    "com.edukit.core",
    "com.edukit.external",
    "com.edukit.globalutils"
})
public class EdukitBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(EdukitBatchApplication.class, args);
    }

} 
