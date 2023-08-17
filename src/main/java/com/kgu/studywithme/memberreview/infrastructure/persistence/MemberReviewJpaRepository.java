package com.kgu.studywithme.memberreview.infrastructure.persistence;

import com.kgu.studywithme.memberreview.domain.MemberReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberReviewJpaRepository extends JpaRepository<MemberReview, Long> {
}
