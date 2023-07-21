package com.kgu.studywithme.studyweekly.infrastructure.repository.query;

import com.kgu.studywithme.studyweekly.domain.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.submit.StudyWeeklySubmit;

import java.util.Optional;

public interface StudyWeeklyHandlingRepository {
    int getNextWeek(final Long studyId);

    Optional<StudyWeekly> getSpecificWeekly(final Long studyId, final int week);

    boolean isLatestWeek(final Long studyId, final int week);

    void deleteSpecificWeekly(final Long studyId, final int week);

    Optional<StudyWeeklySubmit> getSubmittedAssignment(final Long memberId, final Long studyId, final int week);
}
