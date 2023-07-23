package com.kgu.studywithme.study.application.usecase.query;

import com.kgu.studywithme.study.infrastructure.repository.query.dto.ReviewInformation;

public interface QueryReviewByIdUseCase {
    ReviewInformation queryReview(final Query query);

    record Query(
            Long studyId
    ) {
    }
}
