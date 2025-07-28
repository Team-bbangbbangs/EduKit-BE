package com.edukit.core.auth.service;

import com.edukit.core.auth.entity.VerificationCode;
import com.edukit.core.auth.enums.VerificationCodeType;
import com.edukit.core.auth.enums.VerificationStatus;
import com.edukit.core.auth.exception.AuthErrorCode;
import com.edukit.core.auth.exception.AuthException;
import com.edukit.core.auth.repository.VerificationCodeRepository;
import com.edukit.core.auth.service.dto.MemberVerificationData;
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
    public List<MemberVerificationData> issueVerificationCodesForMembers(final List<Member> members) {
        return members.stream()
                .map(member -> {
                    String code = RandomCodeGenerator.generate();
                    VerificationCode verificationCode = VerificationCode.create(member, code,
                            VerificationStatus.PENDING,
                            VerificationCodeType.TEACHER_VERIFICATION);
                    verificationCodeRepository.save(verificationCode);
                    return MemberVerificationData.of(member.getEmail(), member.getMemberUuid(), code);
                })
                .toList();
    }

    public void verifyPasswordResetCode(final Member member, final String inputCode) {
        VerificationCode verificationCode = getValidPasswordResetCodeByMember(member.getId());
        checkVerified(verificationCode, inputCode);
        verificationCode.complete();
    }

    private VerificationCode getValidPasswordResetCodeByMember(final long memberId) {
        return verificationCodeRepository.findTop1ByMemberIdAndTypeAndStatusOrderByIdDesc(memberId,
                        VerificationCodeType.PASSWORD_RESET, VerificationStatus.PENDING)
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
        if (!code.getVerificationCode().equals(inputCode)) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN);
        }
    }
}
