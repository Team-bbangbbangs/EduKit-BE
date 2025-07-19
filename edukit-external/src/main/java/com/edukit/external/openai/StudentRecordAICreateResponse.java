package com.edukit.external.openai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StudentRecordAICreateResponse(
        String description1,
        String description2,
        String description3
) {
}
