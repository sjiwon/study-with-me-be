package com.kgu.studywithme.member.domain.review;

import com.kgu.studywithme.global.BaseEntity;
import com.kgu.studywithme.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "member_review")
public class PeerReview extends BaseEntity<PeerReview> {
    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reviewee_id", referencedColumnName = "id", nullable = false)
    private Member reviewee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reviewer_id", referencedColumnName = "id", nullable = false)
    private Member reviewer;

    private PeerReview(
            final Member reviewee,
            final Member reviewer,
            final String content
    ) {
        this.reviewee = reviewee;
        this.reviewer = reviewer;
        this.content = content;
    }

    public static PeerReview doReview(
            final Member reviewee,
            final Member reviewer,
            final String content
    ) {
        return new PeerReview(reviewee, reviewer, content);
    }

    public void updateReview(final String content) {
        this.content = content;
    }
}
