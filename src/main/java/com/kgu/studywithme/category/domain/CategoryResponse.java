package com.kgu.studywithme.category.domain;

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
