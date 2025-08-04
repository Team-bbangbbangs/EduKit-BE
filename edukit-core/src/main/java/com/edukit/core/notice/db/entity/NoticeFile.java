package com.edukit.core.notice.db.entity;

import com.edukit.core.common.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeFile extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_file_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id")
    private Notice notice;

    @Column(length = 500, nullable = false)
    private String filePath;    // https://dev-cdn.edukit.co.kr/notices/20250725_223935_ea5f18be.jpg

    @Builder(access = AccessLevel.PRIVATE)
    public NoticeFile(final String filePath) {
        this.filePath = filePath;
    }

    public static NoticeFile create(final String filePath) {
        return NoticeFile.builder()
                .filePath(filePath)
                .build();
    }

    public boolean isExcludedFrom(final List<NoticeFile> newNoticeFiles) {
        return !newNoticeFiles.contains(this);
    }

    public void detachNotice() {
        this.notice = null;
    }

    public void attachToNotice(Notice notice) {
        this.notice = notice;
    }

    public boolean isDetachedFromNotice() {
        return this.notice == null;
    }
}
