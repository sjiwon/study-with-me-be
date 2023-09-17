package com.kgu.studywithme.study.application.usecase.query;

import com.kgu.studywithme.study.domain.model.paging.SearchByCategoryCondition;
import org.springframework.data.domain.Pageable;

public record GetStudiesByCategory(
        SearchByCategoryCondition condition,
        Pageable pageable
) {
}
