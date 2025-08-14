package com.edukit.core.auth.db.repository;

import com.edukit.core.auth.db.entity.VerificationCode;
import com.edukit.core.auth.db.enums.VerificationCodeType;
import com.edukit.core.auth.db.enums.VerificationStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    Optional<VerificationCode> findTop1ByMemberIdAndTypeAndStatusOrderByIdDesc(long memberId, VerificationCodeType type,
                                                                               VerificationStatus status);

    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query("UPDATE VerificationCode v SET v.attempts = v.attempts + 1 WHERE v.id = :id")
    void incrementAttempts(@Param("id") Long id);
}
