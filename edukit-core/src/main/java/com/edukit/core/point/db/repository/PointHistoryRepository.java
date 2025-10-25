package com.edukit.core.point.db.repository;

import com.edukit.core.point.db.entity.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
}
