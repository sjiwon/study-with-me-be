package com.kgu.studywithme.studyattendance.domain.repository.query.dto;

import com.querydsl.core.annotations.QueryProjection;

public record StudyAttendanceWeekly(
        Long studyId,
        int week
) {
    @QueryProjection
    public StudyAttendanceWeekly {}
}
