package com.kgu.studywithme.category.application;

import com.kgu.studywithme.category.application.dto.response.CategoryResponse;
import com.kgu.studywithme.category.domain.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {
    public List<CategoryResponse> findAll() {
        return Arrays.stream(Category.values())
                .map(CategoryResponse::new)
                .toList();
    }
}
