package com.kgu.studywithme.member.infrastructure.repository.query.dto.response;

import com.kgu.studywithme.studyattendance.domain.AttendanceStatus;
import com.querydsl.core.annotations.QueryProjection;

public record AttendanceRatio(
        AttendanceStatus status,
        int count
) {
    @QueryProjection
    public AttendanceRatio {
    }
}
