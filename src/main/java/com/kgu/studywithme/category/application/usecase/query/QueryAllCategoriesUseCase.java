package com.kgu.studywithme.category.application.usecase.query;

import com.kgu.studywithme.category.domain.model.CategoryResponse;

import java.util.List;

public interface QueryAllCategoriesUseCase {
    List<CategoryResponse> invoke();
}
