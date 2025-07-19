package com.edukit.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StatusCode {
    OK(200),
    CREATED(201),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    CONFLICT(409),
    TOO_MANY_REQUESTS(429),
    INTERNAL_SERVER_ERROR(500),
    GATEWAY_TIMEOUT(504);

    private final int status;
}
