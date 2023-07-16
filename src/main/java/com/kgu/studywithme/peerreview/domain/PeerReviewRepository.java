package com.kgu.studywithme.peerreview.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PeerReviewRepository extends JpaRepository<PeerReview, Long> {
    // @Query
    @Query("SELECT p.content" +
            " FROM PeerReview p" +
            " WHERE p.revieweeId = :revieweeId")
    List<String> findAllReviewContentByRevieweeId(@Param("revieweeId") Long revieweeId);

    // Query Method
    Optional<PeerReview> findByReviewerIdAndRevieweeId(Long reviewerId, Long revieweeId);
}
