package com.edukit.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "com.edukit.api",
        "com.edukit.core",
        "com.edukit.external",
        "com.edukit.globalutils"
})
@EntityScan(basePackages = {
        "com.edukit.core.entity"
})
@EnableJpaRepositories(basePackages = {
        "com.edukit.core.repository"
})
public class EdukitApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(EdukitApiApplication.class, args);
    }

} 
