package com.kgu.studywithme.study.utils;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.study.presentation.dto.request.StudyCategorySearchRequest;

public record StudyCategoryCondition(
        Category category,
        String sort,
        String type,
        String province,
        String city
) {
    public StudyCategoryCondition(final StudyCategorySearchRequest request) {
        this(
                Category.from(request.category()),
                request.sort(),
                request.type(),
                request.province(),
                request.city()
        );
    }
}
