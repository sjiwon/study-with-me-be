package com.kgu.studywithme.category.application.usecase;

import com.kgu.studywithme.category.domain.model.CategoryResponse;
import com.kgu.studywithme.common.UseCaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.kgu.studywithme.category.domain.model.Category.APTITUDE_NCS;
import static com.kgu.studywithme.category.domain.model.Category.CERTIFICATION;
import static com.kgu.studywithme.category.domain.model.Category.ETC;
import static com.kgu.studywithme.category.domain.model.Category.INTERVIEW;
import static com.kgu.studywithme.category.domain.model.Category.LANGUAGE;
import static com.kgu.studywithme.category.domain.model.Category.PROGRAMMING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Category -> GetAllCategoriesUseCase 테스트")
class GetAllCategoriesUseCaseTest extends UseCaseTest {
    private final GetAllCategoriesUseCase sut = new GetAllCategoriesUseCase();

    @Test
    @DisplayName("전체 스터디 카테고리를 조회한다")
    void findAllCategories() {
        // when
        final List<CategoryResponse> categoryResponse = sut.invoke();

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
