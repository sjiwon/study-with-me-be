package com.kgu.studywithme.studyreview.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyReviewRepository extends JpaRepository<StudyReview, Long> {
    boolean existsByStudyIdAndWriterId(Long studyId, Long memberId);
}
