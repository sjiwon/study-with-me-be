package com.kgu.studywithme.study.application.usecase.query;

import com.kgu.studywithme.study.infrastructure.query.dto.AttendanceInformation;

import java.util.List;

public interface QueryAttendanceByIdUseCase {
    List<AttendanceInformation> invoke(final Query query);

    record Query(
            Long studyId
    ) {
    }
}
