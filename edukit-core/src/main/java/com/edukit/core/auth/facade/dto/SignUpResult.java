package com.edukit.core.auth.facade.dto;

public record SignUpResult(
        String memberUuid
) {
    public static SignUpResult of(final String memberUuid) {
        return new SignUpResult(memberUuid);
    }
}
