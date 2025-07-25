package com.edukit.core.auth.service;

import com.edukit.core.auth.entity.ValidEmail;
import com.edukit.core.auth.exception.AuthErrorCode;
import com.edukit.core.auth.exception.AuthException;
import com.edukit.core.auth.repository.ValidEmailRepository;
import com.edukit.core.member.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ValidEmailRepository validEmailRepository;
    private final MemberRepository memberRepository;

    private static final String EMAIL_SEPARATOR = "@";
    private static final boolean NOT_DELETED = false;

    public void validateCondition(final String email) {
        checkAlreadyRegistered(email);
        validateEmail(email);
    }

    private void checkAlreadyRegistered(final String email) {
        if (memberRepository.existsByEmailAndIsDeleted(email, NOT_DELETED)) {
            throw new AuthException(AuthErrorCode.MEMBER_ALREADY_REGISTERED);
        }
    }

    private void validateEmail(final String email) {
        List<String> validEmails = getValidEmails();
        String domain = extractEmailDomain(email);
        if (!validEmails.contains(domain)) {
            throw new AuthException(AuthErrorCode.INVALID_EMAIL);
        }
    }

    private String extractEmailDomain(final String email) {
        return email.substring(email.indexOf(EMAIL_SEPARATOR) + 1);
    }

    private List<String> getValidEmails() {
        return validEmailRepository.findAll()
                .stream()
                .map(ValidEmail::getValidEmail)
                .toList();
    }
}
