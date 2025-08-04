package com.edukit.core.auth.db.repository;

import com.edukit.core.auth.db.entity.ValidEmail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ValidEmailRepository extends JpaRepository<ValidEmail, Long> {
}
