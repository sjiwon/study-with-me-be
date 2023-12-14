package com.kgu.studywithme.study.application.usecase.query;

import com.kgu.studywithme.study.utils.search.SearchByCategoryCondition;

public record GetStudiesByCategory(
        SearchByCategoryCondition condition,
        int page
) {
}
