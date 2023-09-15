package com.kgu.studywithme.category.presentation;

import com.kgu.studywithme.category.application.usecase.query.QueryAllCategoriesUseCase;
import com.kgu.studywithme.category.domain.model.CategoryResponse;
import com.kgu.studywithme.global.dto.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "2. 카테고리 API")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryApiController {
    private final QueryAllCategoriesUseCase queryAllCategoriesUseCase;

    @Operation(summary = "스터디 카테고리 조회 EndPoint")
    @GetMapping
    public ResponseEntity<ResponseWrapper<List<CategoryResponse>>> findAllCategories() {
        final List<CategoryResponse> result = queryAllCategoriesUseCase.invoke();
        return ResponseEntity.ok(ResponseWrapper.from(result));
    }
}
