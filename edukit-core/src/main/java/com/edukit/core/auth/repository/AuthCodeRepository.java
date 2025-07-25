package com.edukit.core.auth.repository;

import com.edukit.core.auth.entity.AuthCode;
import com.edukit.core.auth.enums.AuthCodeType;
import com.edukit.core.auth.enums.AuthorizeStatus;
import com.edukit.core.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AuthCodeRepository extends JpaRepository<AuthCode, Long> {

    @Query("SELECT ac FROM AuthCode ac WHERE ac.member = :member AND ac.type = :authCodeType AND ac.status = :status")
    Optional<AuthCode> findByMemberAndAuthCodeTypeAndStatus(Member member, AuthCodeType authCodeType, AuthorizeStatus status);
}
