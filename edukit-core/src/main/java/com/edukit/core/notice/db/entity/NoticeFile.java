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
    private String fileKey;    // notices/20250725_223935_ea5f18be.jpg

    @Builder(access = AccessLevel.PRIVATE)
    public NoticeFile(final Notice notice, final String fileKey) {
        this.notice = notice;
        this.fileKey = fileKey;
    }

    public static NoticeFile create(final Notice notice, final String fileKey) {
        return NoticeFile.builder()
                .notice(notice)
                .fileKey(fileKey)
                .build();
    }
}
