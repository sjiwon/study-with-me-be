package com.kgu.studywithme.study.application.usecase.query;

import com.kgu.studywithme.study.utils.search.SearchByRecommendCondition;

public record GetStudiesByRecommend(
        SearchByRecommendCondition condition,
        int page
) {
}
