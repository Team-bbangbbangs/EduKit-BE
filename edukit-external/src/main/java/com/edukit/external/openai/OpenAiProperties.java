package com.edukit.external.openai;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("openai.api")
public record OpenAiProperties(
        String baseUrl,
        String key,
        int connectTimeout,
        int readTimeout
) {
}
