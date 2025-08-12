package com.edukit.core.auth.util;

import com.edukit.core.auth.exception.AuthErrorCode;
import com.edukit.core.auth.exception.AuthException;
import java.util.regex.Pattern;

public class PasswordValidator {

    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 16;

    // 성능 개선을 위해 정규표현식을 단순화
    private static final Pattern VALID_CHARS = Pattern.compile("^[A-Za-z\\d!@#$%^&*()\\-_=+\\[\\]{};:'\",.<>/?`~]*$");

    public static void validatePasswordFormat(final String password) {
        if (password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH) {
            throw new AuthException(AuthErrorCode.INVALID_PASSWORD_FORMAT);
        }

        // 허용되지 않는 문자 체크
        if (!VALID_CHARS.matcher(password).matches()) {
            throw new AuthException(AuthErrorCode.INVALID_PASSWORD_FORMAT);
        }

        // 영문자, 숫자, 특수문자 중 최소 2가지 조합 체크 (성능 최적화)
        boolean hasLetter = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if ("!@#$%^&*()\\-_=+\\[\\]{};:'\",.<>/?`~".indexOf(c) != -1) {
                hasSpecial = true;
            }
        }

        int combinationCount = 0;
        if (hasLetter) {
            combinationCount++;
        }
        if (hasDigit) {
            combinationCount++;
        }
        if (hasSpecial) {
            combinationCount++;
        }

        if (combinationCount < 2) {
            throw new AuthException(AuthErrorCode.INVALID_PASSWORD_FORMAT);
        }

        // 같은 문자 3번 이상 연속 체크 (성능 최적화)
        char[] chars = password.toCharArray();
        for (int i = 0; i < chars.length - 2; i++) {
            if (chars[i] == chars[i + 1] && chars[i] == chars[i + 2]) {
                throw new AuthException(AuthErrorCode.INVALID_PASSWORD_FORMAT);
            }
        }
    }

    public static void validatePasswordEquality(final String newPassword, final String confirmedNewPassword) {
        if (!newPassword.equals(confirmedNewPassword)) {
            throw new AuthException(AuthErrorCode.PASSWORD_CONFIRM_MISMATCH);
        }
    }
}

