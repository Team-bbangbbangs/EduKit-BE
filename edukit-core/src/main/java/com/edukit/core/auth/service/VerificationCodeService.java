package com.edukit.core.auth.service;

import com.edukit.core.auth.entity.VerificationCode;
import com.edukit.core.auth.enums.VerificationCodeType;
import com.edukit.core.auth.enums.VerificationStatus;
import com.edukit.core.auth.exception.AuthErrorCode;
import com.edukit.core.auth.exception.AuthException;
import com.edukit.core.auth.repository.VerificationCodeRepository;
import com.edukit.core.auth.util.RandomCodeGenerator;
import com.edukit.core.member.entity.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VerificationCodeService {

    private final VerificationCodeRepository verificationCodeRepository;

    @Transactional
    public String issueVerificationCode(final Member member, final VerificationCodeType verificationCodeType) {
        String code = RandomCodeGenerator.generate();
        VerificationCode authorizationCode = VerificationCode.create(member, code, VerificationStatus.PENDING,
                verificationCodeType);
        verificationCodeRepository.save(authorizationCode);
        return code;
    }

    @Transactional
    public void issueVerificationCodesForMembers(final List<Member> members) {
        for (Member member : members) {
            String code = RandomCodeGenerator.generate();
            VerificationCode verificationCode = VerificationCode.create(member, code, VerificationStatus.PENDING,
                    VerificationCodeType.TEACHER_VERIFICATION);
            verificationCodeRepository.save(verificationCode);
        }
    }

    public VerificationCode getVerificationCode(final Member member, final VerificationCodeType verificationCodeType) {
        return verificationCodeRepository.findByMemberAndType(member, verificationCodeType)
                .orElseThrow(() -> new AuthException(AuthErrorCode.CODE_NOT_FOUND));
    }
}
