package com.edukit.core.auth.repository;

import com.edukit.core.auth.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
}
