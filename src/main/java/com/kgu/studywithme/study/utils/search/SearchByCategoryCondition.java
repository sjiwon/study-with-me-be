package com.kgu.studywithme.study.utils.search;

import com.kgu.studywithme.category.domain.model.Category;

public record SearchByCategoryCondition(
        Category category,
        SearchSortType sort,
        String type,
        String province,
        String city
) {
    public SearchByCategoryCondition(
            final Long category,
            final String sort,
            final String type,
            final String province,
            final String city
    ) {
        this(
                Category.from(category),
                SearchSortType.from(sort),
                type,
                province,
                city
        );
    }
}
