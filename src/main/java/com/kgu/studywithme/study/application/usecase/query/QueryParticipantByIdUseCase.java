package com.kgu.studywithme.study.application.usecase.query;

import com.kgu.studywithme.study.infrastructure.repository.query.dto.StudyParticipantInformation;

public interface QueryParticipantByIdUseCase {
    StudyParticipantInformation invoke(final Query query);

    record Query(
            Long studyId
    ) {
    }
}
