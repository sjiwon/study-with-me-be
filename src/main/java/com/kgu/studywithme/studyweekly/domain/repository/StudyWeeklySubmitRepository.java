package com.kgu.studywithme.studyweekly.domain.repository;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studyweekly.domain.model.StudyWeeklySubmit;
import com.kgu.studywithme.studyweekly.exception.StudyWeeklyErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudyWeeklySubmitRepository extends JpaRepository<StudyWeeklySubmit, Long> {
    // @Query
    @Query("""
            SELECT sws
            FROM StudyWeeklySubmit sws
            JOIN FETCH sws.weekly sw
            WHERE sw.id = :weeklyId AND sws.participant.id = :participantId
            """)
    Optional<StudyWeeklySubmit> findSubmittedAssignment(
            @Param("weeklyId") final Long weeklyId,
            @Param("participantId") final Long participantId
    );

    default StudyWeeklySubmit getSubmittedAssignment(final Long weeklyId, final Long participantId) {
        return findSubmittedAssignment(weeklyId, participantId)
                .orElseThrow(() -> StudyWithMeException.type(StudyWeeklyErrorCode.SUBMITTED_ASSIGNMENT_NOT_FOUND));
    }

    @StudyWithMeWritableTransactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM StudyWeeklySubmit sws WHERE sws.weekly.id = :weeklyId")
    int deleteFromSpecificWeekly(@Param("weeklyId") final Long weeklyId);

    // Query Method
    boolean existsByWeeklyId(final Long weeklyId);

    int countByWeeklyId(final Long weeklyId);
}
