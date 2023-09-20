package com.kgu.studywithme.study.application.usecase.query;

import com.kgu.studywithme.study.utils.search.SearchByRecommendCondition;
import org.springframework.data.domain.Pageable;

public record GetStudiesByRecommend(
        SearchByRecommendCondition condition,
        Pageable pageable
) {
}
