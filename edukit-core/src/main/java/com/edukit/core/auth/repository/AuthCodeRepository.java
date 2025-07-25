package com.edukit.core.auth.repository;

import com.edukit.core.auth.entity.AuthCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthCodeRepository extends JpaRepository<AuthCode, Long> {
}
