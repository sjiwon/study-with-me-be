package com.kgu.studywithme.member.application.usecase.query;

import com.kgu.studywithme.member.infrastructure.repository.query.dto.response.AppliedStudy;

import java.util.List;

public interface QueryAppliedStudyByIdUseCase {
    List<AppliedStudy> queryAppliedStudy(final Query query);

    record Query(
            Long memberId
    ) {
    }
}
