package com.kgu.studywithme.category.application.service;

import com.kgu.studywithme.category.application.dto.response.CategoryResponse;
import com.kgu.studywithme.common.UseCaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.kgu.studywithme.category.domain.Category.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Category -> CategoryService 테스트")
class QueryAllCategoriesServiceTest extends UseCaseTest {
    private final QueryAllCategoriesService queryAllCategoriesService = new QueryAllCategoriesService();

    @Test
    @DisplayName("전체 스터디 카테고리를 조회한다")
    void findAllCategory() {
        // when
        List<CategoryResponse> categoryResponse = queryAllCategoriesService.findAllCategories();

        // then
        assertAll(
                () -> assertThat(categoryResponse).hasSize(6),
                () -> assertThat(categoryResponse)
                        .extracting("name")
                        .containsExactlyInAnyOrder(
                                LANGUAGE.getName(),
                                INTERVIEW.getName(),
                                PROGRAMMING.getName(),
                                APTITUDE_NCS.getName(),
                                CERTIFICATION.getName(),
                                ETC.getName()
                        )
        );
    }
}
