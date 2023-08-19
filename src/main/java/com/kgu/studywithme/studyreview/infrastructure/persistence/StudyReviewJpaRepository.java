package com.kgu.studywithme.studyreview.infrastructure.persistence;

import com.kgu.studywithme.studyreview.domain.StudyReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyReviewJpaRepository extends JpaRepository<StudyReview, Long> {
    boolean existsByStudyIdAndWriterId(Long studyId, Long memberId);
}
