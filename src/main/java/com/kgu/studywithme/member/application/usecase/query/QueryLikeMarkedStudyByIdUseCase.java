package com.kgu.studywithme.member.application.usecase.query;

import com.kgu.studywithme.member.infrastructure.repository.query.dto.response.LikeMarkedStudy;

import java.util.List;

public interface QueryLikeMarkedStudyByIdUseCase {
    List<LikeMarkedStudy> queryLikeMarkedStudy(final Query query);

    record Query(
            Long memberId
    ) {
    }
}
