package com.edukit.core.auth.service;

import com.edukit.core.auth.entity.AuthCode;
import com.edukit.core.auth.enums.AuthCodeType;
import com.edukit.core.auth.enums.AuthorizeStatus;
import com.edukit.core.auth.repository.AuthCodeRepository;
import com.edukit.core.auth.util.RandomCodeGenerator;
import com.edukit.core.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthCodeService {

    private final AuthCodeRepository authCodeRepository;
    private final RandomCodeGenerator randomCodeGenerator;

    @Transactional
    public String issueVerificationCode(final Member member, final AuthCodeType authCodeType) {
        expireExistingPendingCodeIfExists(member, authCodeType);

        String code = randomCodeGenerator.generate();
        AuthCode authorizationCode = AuthCode.create(member, code, AuthorizeStatus.PENDING, authCodeType);
        authCodeRepository.save(authorizationCode);

        return code;
    }

    private void expireExistingPendingCodeIfExists(final Member member, final AuthCodeType authCodeType) {
        authCodeRepository.findByMemberAndAuthCodeTypeAndStatus(member, authCodeType, AuthorizeStatus.PENDING)
                .ifPresent(existingCode -> {
                    existingCode.updateStatus(AuthorizeStatus.EXPIRED);
                    authCodeRepository.save(existingCode);
                });
    }
}
