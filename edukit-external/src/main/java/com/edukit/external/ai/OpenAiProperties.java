package com.edukit.external.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("ai.chatgpt")
public record OpenAiProperties(
        int connectTimeout,
        int readTimeout
) {
}
