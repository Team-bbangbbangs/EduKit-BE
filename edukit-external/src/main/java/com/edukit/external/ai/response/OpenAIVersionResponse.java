package com.edukit.external.ai.response;

public record OpenAIVersionResponse(
        int versionNumber,
        String content,
        boolean isLast
) {

    public static OpenAIVersionResponse of(final int versionNumber, final String content, final boolean isLast) {
        return new OpenAIVersionResponse(versionNumber, content, isLast);
    }
}
