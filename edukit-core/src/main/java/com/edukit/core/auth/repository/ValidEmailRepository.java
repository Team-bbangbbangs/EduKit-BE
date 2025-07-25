package com.edukit.core.auth.repository;

import com.edukit.core.auth.entity.ValidEmail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ValidEmailRepository extends JpaRepository<ValidEmail, Long> {
}
