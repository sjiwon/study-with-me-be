package com.kgu.studywithme.member.application.usecase.query;

import com.kgu.studywithme.member.infrastructure.repository.query.dto.response.AttendanceRatio;

import java.util.List;

public interface QueryAttendanceRatioByIdUseCase {
    List<AttendanceRatio> queryAttendanceRatio(final Query query);

    record Query(
            Long memberId
    ) {
    }
}