package com.kgu.studywithme.category.application.service;

import com.kgu.studywithme.category.application.dto.CategoryResponse;
import com.kgu.studywithme.common.UseCaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.kgu.studywithme.category.domain.Category.APTITUDE_NCS;
import static com.kgu.studywithme.category.domain.Category.CERTIFICATION;
import static com.kgu.studywithme.category.domain.Category.ETC;
import static com.kgu.studywithme.category.domain.Category.INTERVIEW;
import static com.kgu.studywithme.category.domain.Category.LANGUAGE;
import static com.kgu.studywithme.category.domain.Category.PROGRAMMING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Category -> QueryAllCategoriesService 테스트")
class QueryAllCategoriesServiceTest extends UseCaseTest {
    private final QueryAllCategoriesService queryAllCategoriesService = new QueryAllCategoriesService();

    @Test
    @DisplayName("전체 스터디 카테고리를 조회한다")
    void findAllCategories() {
        // when
        final List<CategoryResponse> categoryResponse = queryAllCategoriesService.invoke();

        // then
        assertAll(
                () -> assertThat(categoryResponse).hasSize(6),
                () -> assertThat(categoryResponse)
                        .map(CategoryResponse::name)
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
