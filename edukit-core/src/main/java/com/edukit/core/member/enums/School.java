package com.edukit.core.member.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum School {
    MIDDLE_SCHOOL("middle"),
    HIGH_SCHOOL("high");

    private final String name;
}
