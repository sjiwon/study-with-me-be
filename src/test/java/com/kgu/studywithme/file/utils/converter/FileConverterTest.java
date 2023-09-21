package com.kgu.studywithme.file.utils.converter;

import com.kgu.studywithme.common.ParallelTest;
import com.kgu.studywithme.file.domain.model.RawFileData;
import com.kgu.studywithme.file.exception.FileErrorCode;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.kgu.studywithme.common.utils.FileMockingUtils.createMultipleMockMultipartFile;
import static com.kgu.studywithme.common.utils.FileMockingUtils.createSingleMockMultipartFile;
import static com.kgu.studywithme.file.domain.model.FileExtension.HWPX;
import static com.kgu.studywithme.file.domain.model.FileExtension.PDF;
import static com.kgu.studywithme.file.domain.model.FileExtension.PNG;
import static com.kgu.studywithme.file.domain.model.FileExtension.TXT;
import static com.kgu.studywithme.file.domain.model.FileUploadType.STUDY_DESCRIPTION_IMAGE;
import static com.kgu.studywithme.file.domain.model.FileUploadType.STUDY_WEEKLY_ASSIGNMENT;
import static com.kgu.studywithme.file.domain.model.FileUploadType.STUDY_WEEKLY_ATTACHMENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("FIle -> FileConverter 테스트")
public class FileConverterTest extends ParallelTest {
    @Nested
    @DisplayName("이미지 파일 Convert")
    class ConvertImageFile {
        @Test
        @DisplayName("MultipartFile이 비어있으면 예외가 발생한다")
        void throwExceptionByFIleIsNotUpload() {
            // given
            final MultipartFile file = new MockMultipartFile("hello.png", new byte[0]);

            // when - then
            assertThatThrownBy(() -> FileConverter.convertImageFile(file, STUDY_DESCRIPTION_IMAGE))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(FileErrorCode.FILE_IS_NOT_UPLOAD.getMessage());
        }

        @Test
        @DisplayName("MultipartFile -> RawFileData로 Converting한다")
        void success() throws IOException {
            // given
            final MultipartFile file = createSingleMockMultipartFile("hello3.pdf", "application/pdf");

            // when
            final RawFileData rawFileData = FileConverter.convertImageFile(file, STUDY_DESCRIPTION_IMAGE);

            // then
            assertAll(
                    () -> assertThat(rawFileData.fileName()).isEqualTo("hello3.pdf"),
                    () -> assertThat(rawFileData.contenType()).isEqualTo("application/pdf"),
                    () -> assertThat(rawFileData.extension()).isEqualTo(PDF),
                    () -> assertThat(rawFileData.uploadType()).isEqualTo(STUDY_DESCRIPTION_IMAGE)
            );
        }
    }

    @Nested
    @DisplayName("과제 제출물 파일 Convert")
    class ConverAssignmentFile {
        @Test
        @DisplayName("MultipartFile이 비어있으면 null을 반환한다")
        void responseNull() {
            // given
            final MultipartFile file = new MockMultipartFile("hello.png", new byte[0]);

            // when
            final RawFileData rawFileData = FileConverter.convertAssignmentFile(file);

            // then
            assertThat(rawFileData).isNull();
        }

        @Test
        @DisplayName("MultipartFile -> RawFileData로 Converting한다")
        void success() throws IOException {
            // given
            final MultipartFile file = createSingleMockMultipartFile("hello3.pdf", "application/pdf");

            // when
            final RawFileData rawFileData = FileConverter.convertAssignmentFile(file);

            // then
            assertAll(
                    () -> assertThat(rawFileData.fileName()).isEqualTo("hello3.pdf"),
                    () -> assertThat(rawFileData.contenType()).isEqualTo("application/pdf"),
                    () -> assertThat(rawFileData.extension()).isEqualTo(PDF),
                    () -> assertThat(rawFileData.uploadType()).isEqualTo(STUDY_WEEKLY_ASSIGNMENT)
            );
        }
    }

    @Nested
    @DisplayName("주차별 첨부파일 Convert")
    class ConvertAttachmentFiles {
        @Test
        @DisplayName("List<MultipartFile>이 비어있으면 빈 리스트를 반환한다")
        void emptyList() {
            // given
            final List<MultipartFile> files = List.of();

            // when
            final List<RawFileData> rawFileData = FileConverter.convertAttachmentFiles(files);

            // then
            assertThat(rawFileData).isEmpty();
        }

        @Test
        @DisplayName("List<MultipartFile> -> List<RawFileData>로 Converting한다")
        void success() throws IOException {
            // given
            final List<MultipartFile> files = List.of(
                    createMultipleMockMultipartFile("hello1.txt", "text/plain"),
                    createMultipleMockMultipartFile("hello2.hwpx", "application/x-hwpml"),
                    createMultipleMockMultipartFile("hello3.pdf", "application/pdf"),
                    createMultipleMockMultipartFile("hello4.png", "image/png")
            );

            // when
            final List<RawFileData> rawFileDatas = FileConverter.convertAttachmentFiles(files);

            // then
            assertAll(
                    () -> assertThat(rawFileDatas).hasSize(4),
                    () -> assertThat(rawFileDatas)
                            .map(RawFileData::fileName)
                            .containsExactlyInAnyOrder("hello1.txt", "hello2.hwpx", "hello3.pdf", "hello4.png"),
                    () -> assertThat(rawFileDatas)
                            .map(RawFileData::contenType)
                            .containsExactlyInAnyOrder("text/plain", "application/x-hwpml", "application/pdf", "image/png"),
                    () -> assertThat(rawFileDatas)
                            .map(RawFileData::extension)
                            .containsExactlyInAnyOrder(TXT, HWPX, PDF, PNG),
                    () -> assertThat(rawFileDatas)
                            .map(RawFileData::uploadType)
                            .containsExactlyInAnyOrder(
                                    STUDY_WEEKLY_ATTACHMENT,
                                    STUDY_WEEKLY_ATTACHMENT,
                                    STUDY_WEEKLY_ATTACHMENT,
                                    STUDY_WEEKLY_ATTACHMENT
                            )
            );
        }
    }
}
