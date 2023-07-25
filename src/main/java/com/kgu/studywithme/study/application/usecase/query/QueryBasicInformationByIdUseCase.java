package com.kgu.studywithme.study.application.usecase.query;

import com.kgu.studywithme.study.infrastructure.repository.query.dto.StudyBasicInformation;

public interface QueryBasicInformationByIdUseCase {
    StudyBasicInformation queryBasicInformation(final Query query);

    record Query(
            Long studyId
    ) {
    }
}
