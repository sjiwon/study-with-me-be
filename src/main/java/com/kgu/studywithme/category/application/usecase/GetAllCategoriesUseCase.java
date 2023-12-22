package com.kgu.studywithme.category.application.usecase;

import com.kgu.studywithme.category.domain.model.Category;
import com.kgu.studywithme.category.domain.model.CategoryResponse;
import com.kgu.studywithme.global.annotation.UseCase;

import java.util.Arrays;
import java.util.List;

@UseCase
public class GetAllCategoriesUseCase {
    public List<CategoryResponse> invoke() {
        return Arrays.stream(Category.values())
                .map(CategoryResponse::new)
                .toList();
    }
}
