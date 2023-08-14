package com.kgu.studywithme.file.domain;

import com.kgu.studywithme.file.exception.FileErrorCode;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static com.kgu.studywithme.file.domain.FileExtension.DOC;
import static com.kgu.studywithme.file.domain.FileExtension.DOCX;
import static com.kgu.studywithme.file.domain.FileExtension.GIF;
import static com.kgu.studywithme.file.domain.FileExtension.HWP;
import static com.kgu.studywithme.file.domain.FileExtension.HWPX;
import static com.kgu.studywithme.file.domain.FileExtension.JPEG;
import static com.kgu.studywithme.file.domain.FileExtension.JPG;
import static com.kgu.studywithme.file.domain.FileExtension.PDF;
import static com.kgu.studywithme.file.domain.FileExtension.PNG;
import static com.kgu.studywithme.file.domain.FileExtension.PPT;
import static com.kgu.studywithme.file.domain.FileExtension.PPTX;
import static com.kgu.studywithme.file.domain.FileExtension.TXT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("File -> FileExtension 테스트")
public class FileExtensionTest {
    @Nested
    @DisplayName("파일 확장자 추출")
    class GetExtensionFromFileName {
        @ParameterizedTest
        @ValueSource(strings = {"hello.mp3", "hello.xls", "hello.alz"})
        @DisplayName("제공하지 않는 파일의 확장자면 예외가 발생한다")
        void throwExceptionByInvalidFileExtension(final String fileName) {
            assertThatThrownBy(() -> FileExtension.getExtensionFromFileName(fileName))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(FileErrorCode.INVALID_FILE_EXTENSION.getMessage());
        }

        @ParameterizedTest
        @MethodSource("validExtension")
        @DisplayName("파일 확장자에 대한 FileExtension을 얻는다")
        void success(final String fileName, final FileExtension extension) {
            assertThat(FileExtension.getExtensionFromFileName(fileName)).isEqualTo(extension);
        }

        private static Stream<Arguments> validExtension() {
            return Stream.of(
                    Arguments.of("hello.jpg", JPG),
                    Arguments.of("hello.jpeg", JPEG),
                    Arguments.of("hello.png", PNG),
                    Arguments.of("hello.gif", GIF),
                    Arguments.of("hello.txt", TXT),
                    Arguments.of("hello.doc", DOC),
                    Arguments.of("hello.docx", DOCX),
                    Arguments.of("hello.hwp", HWP),
                    Arguments.of("hello.hwpx", HWPX),
                    Arguments.of("hello.pdf", PDF),
                    Arguments.of("hello.ppt", PPT),
                    Arguments.of("hello.pptx", PPTX)
            );
        }
    }

    @ParameterizedTest
    @MethodSource("validExtension")
    @DisplayName("제공하는 이미지 확장자인지 확인한다")
    void isValidExtension(final String fileName, final boolean result) {
        assertThat(FileExtension.isValidExtension(fileName)).isEqualTo(result);
    }

    private static Stream<Arguments> validExtension() {
        return Stream.of(
                Arguments.of("hello.jpg", true),
                Arguments.of("hello.jpeg", true),
                Arguments.of("hello.png", true),
                Arguments.of("hello.gif", true),
                Arguments.of("hello.txt", true),
                Arguments.of("hello.doc", true),
                Arguments.of("hello.docx", true),
                Arguments.of("hello.hwp", true),
                Arguments.of("hello.hwpx", true),
                Arguments.of("hello.pdf", true),
                Arguments.of("hello.ppt", true),
                Arguments.of("hello.pptx", true),
                Arguments.of("hello.mp3", false),
                Arguments.of("hello.xls", false),
                Arguments.of("hello.alz", false)
        );
    }

    @ParameterizedTest
    @MethodSource("validImageExtension")
    @DisplayName("제공하는 이미지 파일 확장자인지 확인한다")
    void isImageExtension(final String fileName, final boolean result) {
        assertThat(FileExtension.isValidImageExtension(fileName)).isEqualTo(result);
    }

    private static Stream<Arguments> validImageExtension() {
        return Stream.of(
                Arguments.of("hello.jpg", true),
                Arguments.of("hello.jpeg", true),
                Arguments.of("hello.png", true),
                Arguments.of("hello.gif", true),
                Arguments.of("hello.webp", false),
                Arguments.of("hello.bmp", false)
        );
    }
}
