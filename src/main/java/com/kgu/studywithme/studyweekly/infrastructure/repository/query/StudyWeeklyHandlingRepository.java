package com.kgu.studywithme.studyweekly.infrastructure.repository.query;

public interface StudyWeeklyHandlingRepository {
    int getNextWeek(final Long studyId);
}
