package com.kgu.studywithme.study.application.usecase.query;

import com.kgu.studywithme.study.infrastructure.repository.query.dto.AttendanceInformation;

import java.util.List;

public interface QueryAttendanceByIdUseCase {
    List<AttendanceInformation> queryAttendance(final Query query);

    record Query(
            Long studyId
    ) {
    }
}
