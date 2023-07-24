package com.kgu.studywithme.memberreview.domain;

import com.kgu.studywithme.global.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "member_review")
public class MemberReview extends BaseEntity<MemberReview> {
    @Column(name = "reviewer_id", nullable = false)
    private Long reviewerId;

    @Column(name = "reviewee_id", nullable = false)
    private Long revieweeId;

    @Column(name = "content", nullable = false)
    private String content;

    private MemberReview(
            final Long reviewerId,
            final Long revieweeId,
            final String content
    ) {
        this.reviewerId = reviewerId;
        this.revieweeId = revieweeId;
        this.content = content;
    }

    public static MemberReview doReview(
            final Long reviewerId,
            final Long revieweeId,
            final String content
    ) {
        return new MemberReview(reviewerId, revieweeId, content);
    }

    public void updateReview(final String content) {
        this.content = content;
    }

    public boolean isSameContent(final String updateContent) {
        return this.content.equals(updateContent);
    }
}
