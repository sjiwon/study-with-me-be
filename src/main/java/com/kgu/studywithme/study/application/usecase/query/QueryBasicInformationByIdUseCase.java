package com.kgu.studywithme.study.application.usecase.query;

import com.kgu.studywithme.study.infrastructure.query.dto.StudyBasicInformation;

public interface QueryBasicInformationByIdUseCase {
    StudyBasicInformation invoke(final Query query);

    record Query(
            Long studyId
    ) {
    }
}
