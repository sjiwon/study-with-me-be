package com.kgu.studywithme.study.application.usecase.query;

import com.kgu.studywithme.study.infrastructure.query.dto.WeeklyInformation;

import java.util.List;

public interface QueryWeeklyByIdUseCase {
    List<WeeklyInformation> invoke(final Query query);

    record Query(
            Long studyId
    ) {
    }
}
