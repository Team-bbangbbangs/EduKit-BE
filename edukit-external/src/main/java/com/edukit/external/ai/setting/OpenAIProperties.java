package com.edukit.external.ai.setting;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.ai.openai")
public record OpenAIProperties(
        String baseUrl,
        String apiKey,
        int connectionTimeout,
        int readTimeout
) {
}
