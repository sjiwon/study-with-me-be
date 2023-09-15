package com.kgu.studywithme.studyweekly.application.adapter;

import com.kgu.studywithme.studyweekly.domain.model.StudyWeeklySubmit;
import com.kgu.studywithme.studyweekly.infrastructure.query.dto.AutoAttendanceAndFinishedWeekly;

import java.util.List;
import java.util.Optional;

public interface StudyWeeklyHandlingRepositoryAdapter {
    int getNextWeek(final Long studyId);

    boolean isLatestWeek(final Long studyId, final Long weeklyId);

    void deleteSpecificWeekly(final Long studyId, final Long weeklyId);

    Optional<StudyWeeklySubmit> getSubmittedAssignment(final Long memberId, final Long studyId, final Long weeklyId);

    List<AutoAttendanceAndFinishedWeekly> findAutoAttendanceAndFinishedWeekly();
}
