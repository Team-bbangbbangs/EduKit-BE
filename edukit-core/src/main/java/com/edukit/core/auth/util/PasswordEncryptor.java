package com.edukit.core.auth.util;

public interface PasswordEncryptor {
    String encode(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);
}
