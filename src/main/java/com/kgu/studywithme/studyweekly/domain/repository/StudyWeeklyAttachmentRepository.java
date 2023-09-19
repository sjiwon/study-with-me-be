package com.kgu.studywithme.studyweekly.domain.repository;

import com.kgu.studywithme.global.annotation.StudyWithMeWritableTransactional;
import com.kgu.studywithme.studyweekly.domain.model.StudyWeeklyAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudyWeeklyAttachmentRepository extends JpaRepository<StudyWeeklyAttachment, Long> {
    // @Query
    @StudyWithMeWritableTransactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM StudyWeeklyAttachment swa WHERE swa.weekly.id = :weeklyId")
    int deleteFromSpecificWeekly(@Param("weeklyId") final Long weeklyId);

    // Query Method
    boolean existsByWeeklyId(final Long weeklyId);

    int countByWeeklyId(final Long weeklyId);
}
