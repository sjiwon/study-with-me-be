package com.kgu.studywithme.member.application.usecase.query;

import com.kgu.studywithme.member.infrastructure.repository.query.dto.response.ReceivedReview;

import java.util.List;

public interface QueryReceivedReviewByIdUseCase {
    List<ReceivedReview> queryReceivedReview(final Query query);

    record Query(
            Long memberId
    ) {
    }
}
