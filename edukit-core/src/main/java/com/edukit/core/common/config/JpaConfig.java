package com.edukit.core.common.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "com.edukit.core")
@EnableJpaRepositories(basePackages = "com.edukit.core")
public class JpaConfig {
}
