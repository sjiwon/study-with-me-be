package com.kgu.studywithme.category.application.dto;

import com.kgu.studywithme.category.domain.Category;

public record CategoryResponse(
        Long id,
        String name
) {
    public CategoryResponse(final Category category) {
        this(
                category.getId(),
                category.getName()
        );
    }
}
