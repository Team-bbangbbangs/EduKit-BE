package com.edukit.core.auth.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {

    public static String encode(final String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean matches(final String rawPassword, final String encodedPassword) {
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }
}
