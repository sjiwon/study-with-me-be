package com.kgu.studywithme.category.application;

import com.kgu.studywithme.category.application.dto.response.CategoryResponse;
import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.global.annotation.StudyWithMeReadOnlyTransactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@StudyWithMeReadOnlyTransactional
@RequiredArgsConstructor
public class CategoryService {
    public List<CategoryResponse> findAll() {
        return Arrays.stream(Category.values())
                .map(CategoryResponse::new)
                .toList();
    }
}
