package com.kgu.studywithme.memberreview.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberReviewRepository extends JpaRepository<MemberReview, Long> {
    // @Query
    @Query("SELECT mr.content" +
            " FROM MemberReview mr" +
            " WHERE mr.revieweeId = :revieweeId")
    List<String> findAllReviewContentByRevieweeId(@Param("revieweeId") Long revieweeId);

    // Query Method
    Optional<MemberReview> findByReviewerIdAndRevieweeId(Long reviewerId, Long revieweeId);

    boolean existsByReviewerIdAndRevieweeId(Long reviewerId, Long revieweeId);
}
