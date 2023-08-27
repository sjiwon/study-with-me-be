package com.kgu.studywithme.file.utils.validator;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;

@DisplayName("File -> ImageUploadCategoryConstraintValidator 테스트")
class ImageUploadCategoryConstraintValidatorTest {
    private ImageUploadCategoryConstraintValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new ImageUploadCategoryConstraintValidator();
        context = mock(ConstraintValidatorContext.class);
    }

    @Test
    @DisplayName("허용하지 않는 이미지 업로드 타입이 들어오면 validator를 통과하지 못한다")
    void notAllowedStudyType() {
        // given
        final String unknown = "unknown";

        // when
        final boolean actual = validator.isValid(unknown, context);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("허용하는 이미지 업로드 타입이 들어오면 validator를 통과한다")
    void allowedStudyType() {
        // given
        final String weekly = "weekly";
        final String description = "description";

        // when
        final boolean actual1 = validator.isValid(weekly, context);
        final boolean actual2 = validator.isValid(description, context);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isTrue()
        );
    }
}
