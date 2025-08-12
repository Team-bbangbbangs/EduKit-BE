package com.edukit.core.auth.service;

import com.edukit.core.auth.db.entity.VerificationCode;
import com.edukit.core.auth.db.enums.VerificationCodeType;
import com.edukit.core.auth.db.enums.VerificationStatus;
import com.edukit.core.auth.db.repository.VerificationCodeRepository;
import com.edukit.core.auth.exception.AuthErrorCode;
import com.edukit.core.auth.exception.AuthException;
import com.edukit.core.auth.util.RandomCodeGenerator;
import com.edukit.core.member.db.entity.Member;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
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
    public void checkVerificationCode(final Member member, final String inputCode, final VerificationCodeType verificationCodeType) {
        VerificationCode verificationCode = getValidVerificationCodeByMember(member.getId(), verificationCodeType);
        checkVerified(verificationCode, inputCode);
        verificationCode.verified();
    }

    private VerificationCode getValidVerificationCodeByMember(final long memberId,
                                                              final VerificationCodeType verificationCodeType) {
        return verificationCodeRepository.findTop1ByMemberIdAndTypeAndStatusOrderByIdDesc(memberId,
                        verificationCodeType, VerificationStatus.PENDING)
                .orElseThrow(() -> new AuthException(AuthErrorCode.VERIFICATION_CODE_NOT_FOUND));
    }

    private void checkVerified(final VerificationCode code, final String inputCode) {
        try {
            validateCode(code, inputCode);
        } catch (AuthException e) {
            code.expire();
            throw e;
        }
    }

    private void validateCode(final VerificationCode code, final String inputCode) {
        if (code.isExpired()) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN);
        }
        if (!MessageDigest.isEqual(
                code.getVerificationCode().getBytes(StandardCharsets.UTF_8),
                inputCode.getBytes(StandardCharsets.UTF_8))
        ) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN);
        }
    }
}
