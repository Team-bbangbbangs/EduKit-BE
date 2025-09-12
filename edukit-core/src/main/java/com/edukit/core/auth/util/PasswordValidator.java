package com.edukit.core.auth.util;

import com.edukit.core.auth.exception.AuthErrorCode;
import com.edukit.core.auth.exception.AuthException;
import java.util.regex.Pattern;

public class PasswordValidator {

    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 16;
    private static final String SPECIAL = "!@#$%^&*()\\-_=+\\[\\]{};:'\",.<>/?`~";
    private static final Pattern PASSWORD_FORMAT_PATTERN = Pattern.compile(
            "^(?!.*(.)\\1{2})[A-Za-z\\d" + SPECIAL + "]+$"
    );

    public static void validatePasswordFormat(final String password) {
        if (password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH) {
            throw new AuthException(AuthErrorCode.INVALID_PASSWORD_FORMAT);
        }

        if (!PASSWORD_FORMAT_PATTERN.matcher(password).matches()) {
            throw new AuthException(AuthErrorCode.INVALID_PASSWORD_FORMAT);
        }

        int categoryCount = 0;
        if (password.matches(".*[A-Za-z].*")) {
            categoryCount++; // 영문
        }
        if (password.matches(".*\\d.*")) {
            categoryCount++;       // 숫자
        }
        if (password.matches(".*[" + SPECIAL + "].*")) {
            categoryCount++; // 특수문자
        }

        if (categoryCount < 2) {
            throw new AuthException(AuthErrorCode.INVALID_PASSWORD_FORMAT);
        }
    }
}

