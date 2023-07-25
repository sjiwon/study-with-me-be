package com.kgu.studywithme.study.utils;

import com.kgu.studywithme.study.utils.PagingConstants.SortType;

public record QueryStudyByRecommendCondition(
        Long memberId,
        SortType sort,
        String type,
        String province,
        String city
) {
    public QueryStudyByRecommendCondition(
            final Long memberId,
            final String sort,
            final String type,
            final String province,
            final String city
    ) {
        this(
                memberId,
                SortType.from(sort),
                type,
                province,
                city
        );
    }
}
