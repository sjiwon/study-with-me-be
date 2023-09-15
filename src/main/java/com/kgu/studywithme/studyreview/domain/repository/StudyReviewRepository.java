package com.kgu.studywithme.studyreview.domain.repository;

import com.kgu.studywithme.studyreview.domain.model.StudyReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyReviewRepository extends JpaRepository<StudyReview, Long> {
    boolean existsByStudyIdAndWriterId(final Long studyId, final Long memberId);
}
