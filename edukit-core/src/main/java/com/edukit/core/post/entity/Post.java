package com.edukit.core.post.entity;

import com.edukit.core.common.domain.BaseTimeEntity;
import com.edukit.core.member.entity.Member;
import com.edukit.core.subject.entity.Subject;
import com.edukit.core.post.enums.PostCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PostCategory category;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private int likeCount;

    @Builder(access = AccessLevel.PRIVATE)
    public Post(Member member, Subject subject, PostCategory category, String title, String content, int likeCount) {
        this.member = member;
        this.subject = subject;
        this.category = category;
        this.title = title;
        this.content = content;
        this.likeCount = likeCount;
    }
}
