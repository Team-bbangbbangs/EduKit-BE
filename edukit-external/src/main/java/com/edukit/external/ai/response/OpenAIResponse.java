package com.edukit.external.ai.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenAIResponse(
        String description1,
        String description2,
        String description3
) {
}
