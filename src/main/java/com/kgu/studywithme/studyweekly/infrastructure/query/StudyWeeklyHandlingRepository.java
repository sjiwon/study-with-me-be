package com.kgu.studywithme.studyweekly.infrastructure.query;

import com.kgu.studywithme.studyweekly.domain.submit.StudyWeeklySubmit;
import com.kgu.studywithme.studyweekly.infrastructure.query.dto.AutoAttendanceAndFinishedWeekly;

import java.util.List;
import java.util.Optional;

public interface StudyWeeklyHandlingRepository {
    int getNextWeek(final Long studyId);

    boolean isLatestWeek(final Long studyId, final Long weeklyId);

    void deleteSpecificWeekly(final Long studyId, final Long weeklyId);

    Optional<StudyWeeklySubmit> getSubmittedAssignment(final Long memberId, final Long studyId, final Long weeklyId);

    List<AutoAttendanceAndFinishedWeekly> findAutoAttendanceAndFinishedWeekly();
}
