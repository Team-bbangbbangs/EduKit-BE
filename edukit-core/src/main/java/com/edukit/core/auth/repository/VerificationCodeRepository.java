package com.edukit.core.auth.repository;

import com.edukit.core.auth.entity.VerificationCode;
import com.edukit.core.auth.enums.VerificationCodeType;
import com.edukit.core.auth.enums.VerificationStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    Optional<VerificationCode> findTop1ByMemberIdAndTypeAndStatusOrderByIdDesc(long memberId, VerificationCodeType type,
                                                                               VerificationStatus status);
}
