package com.kgu.studywithme.peerreview.domain;

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
public class PeerReview extends BaseEntity<PeerReview> {
    @Column(name = "reviewer_id", nullable = false)
    private Long reviewerId;

    @Column(name = "reviewee_id", nullable = false)
    private Long revieweeId;

    @Column(name = "content", nullable = false)
    private String content;

    private PeerReview(
            final Long reviewerId,
            final Long revieweeId,
            final String content
    ) {
        this.reviewerId = reviewerId;
        this.revieweeId = revieweeId;
        this.content = content;
    }

    public static PeerReview doReview(
            final Long reviewerId,
            final Long revieweeId,
            final String content
    ) {
        return new PeerReview(reviewerId, revieweeId, content);
    }

    public void updateReview(final String content) {
        this.content = content;
    }

    public boolean isReviewSame(final String updateContent) {
        return this.content.equals(updateContent);
    }
}
