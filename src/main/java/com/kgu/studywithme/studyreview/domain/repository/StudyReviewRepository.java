package com.kgu.studywithme.studyreview.domain.repository;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studyreview.domain.model.StudyReview;
import com.kgu.studywithme.studyreview.exception.StudyReviewErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudyReviewRepository extends JpaRepository<StudyReview, Long> {
    // @Query
    @Query("""
            SELECT sr
            FROM StudyReview sr
            JOIN FETCH sr.writer
            WHERE sr.id = :id
            """)
    Optional<StudyReview> findByIdWithWriter(@Param("id") final Long id);

    default StudyReview getByIdWithWriter(final Long id) {
        return findByIdWithWriter(id)
                .orElseThrow(() -> StudyWithMeException.type(StudyReviewErrorCode.STUDY_REVIEW_NOT_FOUND));
    }

    // Query Method
    boolean existsByStudyIdAndWriterId(final Long studyId, final Long memberId);
}
