package com.kgu.studywithme.member.application.usecase.query;

import com.kgu.studywithme.member.infrastructure.query.dto.GraduatedStudy;

import java.util.List;

public interface QueryGraduatedStudyByIdUseCase {
    List<GraduatedStudy> invoke(final Query query);

    record Query(
            Long memberId
    ) {
    }
}
