package com.kgu.studywithme.studyattendance.application.adapter;

import com.kgu.studywithme.studyattendance.infrastructure.query.dto.NonAttendanceWeekly;

import java.util.List;

public interface StudyAttendanceHandlingRepositoryAdapter {
    List<NonAttendanceWeekly> findNonAttendanceInformation();
}
