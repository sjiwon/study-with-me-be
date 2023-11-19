package com.kgu.studywithme.studyweekly.domain.repository;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studyweekly.domain.model.StudyWeekly;
import com.kgu.studywithme.studyweekly.exception.StudyWeeklyErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudyWeeklyRepository extends JpaRepository<StudyWeekly, Long> {
    default StudyWeekly getById(final Long id) {
        return findById(id)
                .orElseThrow(() -> StudyWithMeException.type(StudyWeeklyErrorCode.WEEKLY_NOT_FOUND));
    }

    // @Query
    @Query("SELECT sw.week" +
            " FROM StudyWeekly sw" +
            " WHERE sw.study.id = :studyId" +
            " ORDER BY sw.week DESC")
    List<Integer> findWeekByStudyId(@Param("studyId") final Long studyId);

    default int getNextWeek(final Long studyId) {
        final List<Integer> weeks = findWeekByStudyId(studyId);

        if (weeks.isEmpty()) {
            return 1;
        }
        return weeks.get(0) + 1;
    }

    @Query("SELECT sw.id" +
            " FROM StudyWeekly sw" +
            " WHERE sw.study.id = :studyId" +
            " ORDER BY sw.id DESC")
    List<Long> findIdByStudyId(@Param("studyId") final Long studyId);

    default boolean isLatestWeek(final Long studyId, final Long weeklyId) {
        final List<Long> weeklyIds = findIdByStudyId(studyId);

        if (weeklyIds.isEmpty()) {
            return true;
        }
        return weeklyIds.get(0).equals(weeklyId);
    }
}
