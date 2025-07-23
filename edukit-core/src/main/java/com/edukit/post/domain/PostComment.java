package com.edukit.post.domain;

import com.edukit.common.domain.BaseTimeEntity;
import com.edukit.member.domain.Member;
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
public class PostComment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "parent_comment_id", nullable = false)
    private Long parentCommentId = 0L;

    @Column(nullable = false)
    private String content;

    @Builder(access = AccessLevel.PRIVATE)
    public PostComment(Post post, Member member, Long parentCommentId, String content) {
        this.post = post;
        this.member = member;
        this.parentCommentId = parentCommentId;
        this.content = content;
    }
}
