package com.edukit.core.auth.jwt.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenType {
    ACCESS("ACCESS"),
    REFRESH("REFRESH");

    private final String type;
}
