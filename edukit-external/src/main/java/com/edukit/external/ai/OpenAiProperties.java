package com.edukit.external.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("openai.api")
public record OpenAiProperties(
        String baseUrl,
        String key,
        int connectTimeout,
        int readTimeout
) {
}
