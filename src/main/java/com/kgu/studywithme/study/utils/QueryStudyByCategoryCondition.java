package com.kgu.studywithme.study.utils;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.study.utils.PagingConstants.SortType;

public record QueryStudyByCategoryCondition(
        Category category,
        SortType sort,
        String type,
        String province,
        String city
) {
    public QueryStudyByCategoryCondition(
            final Long category,
            final String sort,
            final String type,
            final String province,
            final String city
    ) {
        this(
                Category.from(category),
                SortType.from(sort),
                type,
                province,
                city
        );
    }
}
