package com.kgu.studywithme.category.application.service;

import com.kgu.studywithme.category.application.dto.CategoryResponse;
import com.kgu.studywithme.category.application.usecase.query.QueryAllCategoriesUseCase;
import com.kgu.studywithme.category.domain.Category;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class QueryAllCategoriesService implements QueryAllCategoriesUseCase {
    @Override
    public List<CategoryResponse> findAllCategories() {
        return Arrays.stream(Category.values())
                .map(CategoryResponse::new)
                .toList();
    }
}
