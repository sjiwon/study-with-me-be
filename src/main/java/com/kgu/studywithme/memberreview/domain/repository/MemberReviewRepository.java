package com.kgu.studywithme.memberreview.domain.repository;

import com.kgu.studywithme.memberreview.domain.model.MemberReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberReviewRepository extends JpaRepository<MemberReview, Long> {
    Optional<MemberReview> findByReviewerIdAndRevieweeId(final Long reviewerId, final Long revieweeId);

    boolean existsByReviewerIdAndRevieweeId(final Long reviewerId, final Long memberId);
}
