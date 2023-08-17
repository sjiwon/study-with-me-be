package com.kgu.studywithme.studyattendance.infrastructure.query.dto;

import com.querydsl.core.annotations.QueryProjection;

public record NonAttendanceWeekly(
        Long studyId,
        int week,
        Long participantId
) {
    @QueryProjection
    public NonAttendanceWeekly {
    }
}
