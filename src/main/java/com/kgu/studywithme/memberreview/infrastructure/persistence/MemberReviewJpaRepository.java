package com.kgu.studywithme.memberreview.infrastructure.persistence;

import com.kgu.studywithme.memberreview.domain.MemberReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberReviewJpaRepository extends JpaRepository<MemberReview, Long> {
    @Query("SELECT mr" +
            " FROM MemberReview mr" +
            " WHERE mr.reviewerId = :reviewerId AND mr.revieweeId = :revieweeId")
    Optional<MemberReview> getWrittenReviewForReviewee(
            @Param("reviewerId") final Long reviewerId,
            @Param("revieweeId") final Long revieweeId
    );

    boolean existsByReviewerIdAndRevieweeId(final Long reviewerId, final Long memberId);
}
