package com.edukit.core.auth.service;

import com.edukit.core.auth.entity.VerificationCode;
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
public class VerificationCodeService {

    private final AuthCodeRepository authCodeRepository;

    @Transactional
    public String issueVerificationCode(final Member member, final AuthCodeType authCodeType) {
        String code = RandomCodeGenerator.generate();
        VerificationCode authorizationCode = VerificationCode.create(member, code, AuthorizeStatus.PENDING, authCodeType);
        authCodeRepository.save(authorizationCode);
        return code;
    }
}
