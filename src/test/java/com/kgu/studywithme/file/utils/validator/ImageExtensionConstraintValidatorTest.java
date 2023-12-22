package com.kgu.studywithme.file.utils.validator;

import com.kgu.studywithme.common.ParallelTest;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static com.kgu.studywithme.common.utils.FileMockingUtils.createSingleMockMultipartFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("File -> ImageExtensionConstraintValidator 테스트")
class ImageExtensionConstraintValidatorTest extends ParallelTest {
    private final ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
    private final ImageExtensionConstraintValidator sut = new ImageExtensionConstraintValidator();

    @Test
    @DisplayName("파일이 비어있으면 validator를 통과한다")
    void emptyFile() {
        // given
        final MultipartFile file = new MockMultipartFile("file", new byte[0]);

        // when
        final boolean actual = sut.isValid(file, null);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("허용하지 않는 파일 확장자면 validator를 통과하지 못한다")
    void notAllowedExtension() {
        // given
        final MultipartFile file = createSingleMockMultipartFile("hello5.webp", "image/webp");

        // when
        final boolean actual = sut.isValid(file, context);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("허용하는 확장자면 validator를 통과한다")
    void allowedExtension() {
        // given
        final MultipartFile file = createSingleMockMultipartFile("hello4.png", "image/png");

        // when
        final boolean actual = sut.isValid(file, context);

        // then
        assertThat(actual).isTrue();
    }
}
