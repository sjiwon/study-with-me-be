package com.kgu.studywithme.member.application.usecase.query;

import com.kgu.studywithme.member.infrastructure.query.dto.AppliedStudy;

import java.util.List;

public interface QueryAppliedStudyByIdUseCase {
    List<AppliedStudy> invoke(final Query query);

    record Query(
            Long memberId
    ) {
    }
}
