package com.kgu.studywithme.file.utils.validator;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.kgu.studywithme.common.utils.FileMockingUtils.createSingleMockMultipartFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;

@DisplayName("File -> FileExistsConstraintValidator 테스트")
public class FileExistsConstraintValidatorTest {
    private FileExistsConstraintValidator validator;
    private ConstraintValidatorContext context;
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @BeforeEach
    void setUp() {
        validator = new FileExistsConstraintValidator();
        context = mock(ConstraintValidatorContext.class);
        builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
    }

    @Test
    @DisplayName("파일이 비어있으면 validator를 통과하지 못한다")
    void emptyFile() {
        // given
        final MultipartFile nullFile = null;
        final MultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);

        // when
        final boolean actual1 = validator.isValid(nullFile, null);
        final boolean actual2 = validator.isValid(emptyFile, null);

        // then
        assertAll(
                () -> assertThat(actual1).isFalse(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("파일이 존재하면 validator를 통과한다")
    void fileExists() throws IOException {
        // given
        final MultipartFile file = createSingleMockMultipartFile("hello4.png", "image/png");

        // when
        final boolean actual = validator.isValid(file, null);

        // then
        assertThat(actual).isTrue();
    }
}
