package com.kgu.studywithme.member.infrastructure.query.dto;

import com.kgu.studywithme.studyattendance.domain.AttendanceStatus;
import com.querydsl.core.annotations.QueryProjection;

public record AttendanceRatio(
        AttendanceStatus status,
        long count
) {
    @QueryProjection
    public AttendanceRatio {
    }
}
