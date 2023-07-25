package com.kgu.studywithme.study.application.usecase.query;

import com.kgu.studywithme.study.infrastructure.repository.query.dto.StudyApplicantInformation;

import java.util.List;

public interface QueryApplicantByIdUseCase {
    List<StudyApplicantInformation> queryApplicant(final Query query);

    record Query(
            Long studyId
    ) {
    }
}
