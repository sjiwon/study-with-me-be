package com.kgu.studywithme.category.application.usecase;

import com.kgu.studywithme.category.domain.model.Category;
import com.kgu.studywithme.category.domain.model.CategoryResponse;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class GetAllCategoriesUseCase {
    public List<CategoryResponse> invoke() {
        return Arrays.stream(Category.values())
                .map(CategoryResponse::new)
                .toList();
    }
}
