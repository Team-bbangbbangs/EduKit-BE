package com.edukit.core.auth.db.repository;

import com.edukit.core.auth.db.entity.VerificationCode;
import com.edukit.core.auth.db.enums.VerificationCodeType;
import com.edukit.core.auth.db.enums.VerificationStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    Optional<VerificationCode> findTop1ByMemberIdAndTypeAndStatusOrderByIdDesc(long memberId, VerificationCodeType type,
                                                                               VerificationStatus status);
}
