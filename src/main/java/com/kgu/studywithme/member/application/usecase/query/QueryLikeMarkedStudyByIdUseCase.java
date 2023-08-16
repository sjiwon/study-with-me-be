package com.kgu.studywithme.member.application.usecase.query;

import com.kgu.studywithme.member.infrastructure.query.dto.LikeMarkedStudy;

import java.util.List;

public interface QueryLikeMarkedStudyByIdUseCase {
    List<LikeMarkedStudy> invoke(final Query query);

    record Query(
            Long memberId
    ) {
    }
}
