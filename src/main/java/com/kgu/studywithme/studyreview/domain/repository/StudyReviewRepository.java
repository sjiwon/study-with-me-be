package com.kgu.studywithme.studyreview.domain.repository;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studyreview.domain.model.StudyReview;
import com.kgu.studywithme.studyreview.exception.StudyReviewErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyReviewRepository extends JpaRepository<StudyReview, Long> {
    default StudyReview getById(final Long id) {
        return findById(id)
                .orElseThrow(() -> StudyWithMeException.type(StudyReviewErrorCode.STUDY_REVIEW_NOT_FOUND));
    }

    // Method Query
    boolean existsByStudyIdAndWriterId(final Long studyId, final Long memberId);
}
