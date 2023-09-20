package com.kgu.studywithme.study.application.usecase.query;

import com.kgu.studywithme.study.utils.search.SearchByCategoryCondition;
import org.springframework.data.domain.Pageable;

public record GetStudiesByCategory(
        SearchByCategoryCondition condition,
        Pageable pageable
) {
}
