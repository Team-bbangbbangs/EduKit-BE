package com.edukit.api;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.edukit.api",
        "com.edukit.core",
        "com.edukit.common",
        "com.edukit.external"
})
public class EdukitApiApplication {

    private static final String TIMEZONE_KST = "Asia/Seoul";

    public static void main(String[] args) {
        SpringApplication.run(EdukitApiApplication.class, args);
    }

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone(TIMEZONE_KST));
    }

}
