package com.kgu.studywithme.category.domain;

import com.kgu.studywithme.category.exception.CategoryErrorCode;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.kgu.studywithme.category.domain.Category.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Category -> 도메인 [Category] 테스트")
class CategoryTest {
    @Nested
    @DisplayName("단건 카테고리 조회")
    class FindOne {
        @Test
        @DisplayName("특정 카테고리를 조회한다")
        void findSpecificCategory() {
            // given
            final long language = 1L;
            final long interview = 2L;
            final long programming = 3L;
            final long aptituteAndNcs = 4L;
            final long certification = 5L;
            final long etc = 6L;

            // when - then
            assertAll(
                    () -> assertThat(Category.from(language)).isEqualTo(LANGUAGE),
                    () -> assertThat(Category.from(interview)).isEqualTo(INTERVIEW),
                    () -> assertThat(Category.from(programming)).isEqualTo(PROGRAMMING),
                    () -> assertThat(Category.from(aptituteAndNcs)).isEqualTo(APTITUDE_NCS),
                    () -> assertThat(Category.from(certification)).isEqualTo(CERTIFICATION),
                    () -> assertThat(Category.from(etc)).isEqualTo(ETC)
            );
        }

        @Test
        @DisplayName("없는 카테고리를 조회하면 예외가 발생한다")
        void throwExceptionByfindAnonymousCategory() {
            // given
            final long anonymous = 100000000L;

            // when - then
            assertThatThrownBy(() -> Category.from(anonymous))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(CategoryErrorCode.CATEGORY_NOT_EXIST.getMessage());
        }
    }

    @Nested
    @DisplayName("N건 카테고리 조회")
    class FindMany {
        @Test
        @DisplayName("카테고리들을 조회한다")
        void findSpecificCategory() {
            // given
            final Set<Long> ids = Set.of(1L, 2L, 3L);

            // when
            final Set<Category> result = Category.of(ids);

            // then
            assertThat(result).containsExactlyInAnyOrder(LANGUAGE, INTERVIEW, PROGRAMMING);
        }

        @Test
        @DisplayName("없는 카테고리를 조회하면 예외가 발생한다")
        void throwExceptionByfindAnonymousCategory() {
            // given
            final Set<Long> ids = Set.of(1L, 2L, 3L, 100000L);

            // when - then
            assertThatThrownBy(() -> Category.of(ids))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(CategoryErrorCode.CATEGORY_NOT_EXIST.getMessage());
        }
    }
}
