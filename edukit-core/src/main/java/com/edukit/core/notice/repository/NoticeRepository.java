package com.edukit.core.notice.repository;

import com.edukit.core.notice.entity.Notice;
import com.edukit.core.notice.enums.NoticeCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    Page<Notice> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<Notice> findAllByCategoryOrderByCreatedAtDesc(NoticeCategory category, Pageable pageable);
}
