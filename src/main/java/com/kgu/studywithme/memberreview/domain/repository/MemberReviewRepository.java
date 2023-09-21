package com.kgu.studywithme.memberreview.domain.repository;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.memberreview.domain.model.MemberReview;
import com.kgu.studywithme.memberreview.exception.MemberReviewErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberReviewRepository extends JpaRepository<MemberReview, Long> {
    Optional<MemberReview> findByReviewerIdAndRevieweeId(final Long reviewerId, final Long revieweeId);

    default MemberReview getWrittenReview(final Long reviewerId, final Long revieweeId) {
        return findByReviewerIdAndRevieweeId(reviewerId, revieweeId)
                .orElseThrow(() -> StudyWithMeException.type(MemberReviewErrorCode.MEMBER_REVIEW_NOT_FOUND));
    }

    boolean existsByReviewerIdAndRevieweeId(final Long reviewerId, final Long memberId);
}
