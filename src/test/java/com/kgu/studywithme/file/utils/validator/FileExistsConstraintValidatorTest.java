package com.kgu.studywithme.file.utils.validator;

import com.kgu.studywithme.common.ParallelTest;
import jakarta.validation.ConstraintValidatorContext;
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
public class FileExistsConstraintValidatorTest extends ParallelTest {
    private final ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
    private final FileExistsConstraintValidator sut = new FileExistsConstraintValidator();

    @Test
    @DisplayName("파일이 비어있으면 validator를 통과하지 못한다")
    void emptyFile() {
        // given
        final MultipartFile nullFile = null;
        final MultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);

        // when
        final boolean actual1 = sut.isValid(nullFile, context);
        final boolean actual2 = sut.isValid(emptyFile, context);

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
        final boolean actual = sut.isValid(file, context);

        // then
        assertThat(actual).isTrue();
    }
}
