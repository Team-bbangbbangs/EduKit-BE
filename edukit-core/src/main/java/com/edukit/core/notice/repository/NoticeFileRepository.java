package com.edukit.core.notice.repository;

import com.edukit.core.notice.entity.Notice;
import com.edukit.core.notice.entity.NoticeFile;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeFileRepository extends JpaRepository<NoticeFile, Long> {
    List<NoticeFile> findByNotice(Notice notice);
}
