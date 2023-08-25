package com.kgu.studywithme.memberreview.domain;

import com.kgu.studywithme.global.BaseEntity;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.memberreview.exception.MemberReviewErrorCode;
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
        validateReviewSameAsBefore(content);
        this.content = content;
    }

    private void validateReviewSameAsBefore(final String updateContent) {
        if (this.content.equals(updateContent)) {
            throw StudyWithMeException.type(MemberReviewErrorCode.REVIEW_SAME_AS_BEFORE);
        }
    }
}
