package com.kgu.studywithme.memberreview.domain.model;

import com.kgu.studywithme.global.BaseEntity;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.memberreview.exception.MemberReviewErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "member_review")
public class MemberReview extends BaseEntity<MemberReview> {
    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reviewer_id", referencedColumnName = "id", nullable = false)
    private Member reviewer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reviewee_id", referencedColumnName = "id", nullable = false)
    private Member reviewee;

    private MemberReview(final Member reviewer, final Member reviewee, final String content) {
        this.reviewer = reviewer;
        this.reviewee = reviewee;
        this.content = content;
    }

    public static MemberReview doReview(final Member reviewer, final Member reviewee, final String content) {
        return new MemberReview(reviewer, reviewee, content);
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
