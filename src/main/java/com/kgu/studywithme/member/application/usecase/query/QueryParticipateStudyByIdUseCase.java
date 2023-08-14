package com.kgu.studywithme.member.application.usecase.query;

import com.kgu.studywithme.member.infrastructure.repository.query.dto.ParticipateStudy;

import java.util.List;

public interface QueryParticipateStudyByIdUseCase {
    List<ParticipateStudy> invoke(final Query query);

    record Query(
            Long memberId
    ) {
    }
}
