package com.kgu.studywithme.member.application.usecase.query;

import com.kgu.studywithme.member.infrastructure.repository.query.dto.ReceivedReview;

import java.util.List;

public interface QueryReceivedReviewByIdUseCase {
    List<ReceivedReview> invoke(final Query query);

    record Query(
            Long memberId
    ) {
    }
}
