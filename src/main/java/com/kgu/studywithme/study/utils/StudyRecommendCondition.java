package com.kgu.studywithme.study.utils;

import com.kgu.studywithme.study.presentation.dto.request.StudyRecommendSearchRequest;

public record StudyRecommendCondition(
        Long memberId,
        String sort,
        String type,
        String province,
        String city
) {
    public StudyRecommendCondition(
            final Long memberId,
            final StudyRecommendSearchRequest request
    ) {
        this(
                memberId,
                request.sort(),
                request.type(),
                request.province(),
                request.city()
        );
    }
}
