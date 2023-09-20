package com.kgu.studywithme.study.utils.search;

public record SearchByRecommendCondition(
        Long memberId,
        SearchSortType sort,
        String type,
        String province,
        String city
) {
    public SearchByRecommendCondition(
            final Long memberId,
            final String sort,
            final String type,
            final String province,
            final String city
    ) {
        this(
                memberId,
                SearchSortType.from(sort),
                type,
                province,
                city
        );
    }
}
