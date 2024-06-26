package com.kgu.studywithme.studyweekly.domain.repository.query.dto;

import com.querydsl.core.annotations.QueryProjection;

public record AutoAttendanceAndFinishedWeekly(
        Long studyId,
        int week
) {
    @QueryProjection
    public AutoAttendanceAndFinishedWeekly {
    }
}
