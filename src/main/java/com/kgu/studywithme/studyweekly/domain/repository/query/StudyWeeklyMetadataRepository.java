package com.kgu.studywithme.studyweekly.domain.repository.query;

import com.kgu.studywithme.studyweekly.domain.repository.query.dto.AutoAttendanceAndFinishedWeekly;

import java.util.List;

public interface StudyWeeklyMetadataRepository {
    List<AutoAttendanceAndFinishedWeekly> findAutoAttendanceAndFinishedWeekly();
}
