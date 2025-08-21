package com.edukit.core.notice.db.repository;

import com.edukit.core.notice.db.entity.Notice;
import com.edukit.core.notice.db.entity.NoticeFile;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeFileRepository extends JpaRepository<NoticeFile, Long> {
    List<NoticeFile> findByNotice(Notice notice);
}
