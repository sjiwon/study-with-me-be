package com.kgu.studywithme.file.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.file.domain.RawFileData;
import com.kgu.studywithme.file.exception.FileErrorCode;
import com.kgu.studywithme.file.infrastructure.s3.S3FileUploader;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;

import static com.kgu.studywithme.common.utils.FileMockingUtils.createSingleMockMultipartFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("File -> S3FileUploader 테스트")
class S3FileUploaderTest extends UseCaseTest {
    @InjectMocks
    private S3FileUploader uploader;

    @Mock
    private S3Template s3Template;

    @Mock
    private S3Resource s3Resource;

    private static final String BUCKET = "bucket";
    private static final String IMAGE = "images";
    private static final String ATTACHMENT = "attachments";
    private static final String SUBMIT = "submits";
    private static final RawFileData NULL_FILE_DATA = null;

    @BeforeEach
    void setUp() {
        uploader = new S3FileUploader(s3Template, BUCKET);
    }

    @Nested
    @DisplayName("스터디 생성 시 설명 내부 이미지 업로드")
    class uploadDescriptionImage {
        @Test
        @DisplayName("파일을 전송하지 않았거나 파일의 사이즈가 0이면 업로드가 불가능하다")
        void throwExceptionByFileIsEmpty() {
            assertThatThrownBy(() -> uploader.uploadStudyDescriptionImage(NULL_FILE_DATA))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(FileErrorCode.FILE_IS_NOT_UPLOAD.getMessage());
        }

        @Test
        @DisplayName("스터디 설명 이미지를 업로드한다")
        void success() throws Exception {
            // given
            final URL mockUrl = new URL(createUploadLink(IMAGE, "hello4.png"));
            given(s3Template.upload(any(), any(), any(), any())).willReturn(s3Resource);
            given(s3Resource.getURL()).willReturn(mockUrl);

            // when
            final MultipartFile file = createSingleMockMultipartFile("hello4.png", "image/png");
            final RawFileData fileData = new RawFileData(file.getInputStream(), file.getContentType(), file.getOriginalFilename());
            final String uploadUrl = uploader.uploadStudyDescriptionImage(fileData);

            // then
            assertAll(
                    () -> verify(s3Template, times(1)).upload(any(), any(), any(), any()),
                    () -> assertThat(uploadUrl).isEqualTo(mockUrl.toString())
            );
        }
    }

    @Nested
    @DisplayName("Weekly 글 내부 이미지 업로드")
    class uploadWeeklyImage {
        @Test
        @DisplayName("파일을 전송하지 않았거나 파일의 사이즈가 0이면 업로드가 불가능하다")
        void throwExceptionByFileIsEmpty() {
            assertThatThrownBy(() -> uploader.uploadWeeklyImage(NULL_FILE_DATA))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(FileErrorCode.FILE_IS_NOT_UPLOAD.getMessage());
        }

        @Test
        @DisplayName("Weekly 글 내부 이미지를 업로드한다")
        void success() throws Exception {
            // given
            final URL mockUrl = new URL(createUploadLink(IMAGE, "hello4.png"));
            given(s3Template.upload(any(), any(), any(), any())).willReturn(s3Resource);
            given(s3Resource.getURL()).willReturn(mockUrl);

            // when
            final MultipartFile file = createSingleMockMultipartFile("hello4.png", "image/png");
            final RawFileData fileData = new RawFileData(file.getInputStream(), file.getContentType(), file.getOriginalFilename());
            final String uploadUrl = uploader.uploadWeeklyImage(fileData);

            // then
            assertAll(
                    () -> verify(s3Template, times(1)).upload(any(), any(), any(), any()),
                    () -> assertThat(uploadUrl).isEqualTo(mockUrl.toString())
            );
        }
    }

    @Nested
    @DisplayName("Weekly 글 첨부파일 업로드")
    class uploadWeeklyAttachments {
        @Test
        @DisplayName("파일을 전송하지 않았거나 파일의 사이즈가 0이면 업로드가 불가능하다")
        void throwExceptionByFileIsEmpty() {
            assertThatThrownBy(() -> uploader.uploadWeeklyAttachment(NULL_FILE_DATA))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(FileErrorCode.FILE_IS_NOT_UPLOAD.getMessage());
        }

        @Test
        @DisplayName("Weekly 글 첨부파일을 업로드한다")
        void success() throws Exception {
            // given
            final URL mockUrl = new URL(createUploadLink(ATTACHMENT, "hello1.txt"));
            given(s3Template.upload(any(), any(), any(), any())).willReturn(s3Resource);
            given(s3Resource.getURL()).willReturn(mockUrl);

            // when
            final MultipartFile file = createSingleMockMultipartFile("hello1.txt", "text/plain");
            final RawFileData fileData = new RawFileData(file.getInputStream(), file.getContentType(), file.getOriginalFilename());
            final String uploadUrl = uploader.uploadWeeklyAttachment(fileData);

            // then
            assertAll(
                    () -> verify(s3Template, times(1)).upload(any(), any(), any(), any()),
                    () -> assertThat(uploadUrl).isEqualTo(mockUrl.toString())
            );
        }
    }

    @Nested
    @DisplayName("Weekly 과제 제출")
    class uploadWeeklySubmit {
        @Test
        @DisplayName("파일을 전송하지 않았거나 파일의 사이즈가 0이면 업로드가 불가능하다")
        void throwExceptionByFileIsEmpty() {
            assertThatThrownBy(() -> uploader.uploadWeeklySubmit(NULL_FILE_DATA))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(FileErrorCode.FILE_IS_NOT_UPLOAD.getMessage());
        }

        @Test
        @DisplayName("Weekly 과제를 업로드한다")
        void success() throws Exception {
            // given
            final URL mockUrl = new URL(createUploadLink(SUBMIT, "hello3.pdf"));
            given(s3Template.upload(any(), any(), any(), any())).willReturn(s3Resource);
            given(s3Resource.getURL()).willReturn(mockUrl);

            // when
            final MultipartFile file = createSingleMockMultipartFile("hello3.pdf", "application/pdf");
            final RawFileData fileData = new RawFileData(file.getInputStream(), file.getContentType(), file.getOriginalFilename());
            final String uploadUrl = uploader.uploadWeeklySubmit(fileData);

            // then
            assertAll(
                    () -> verify(s3Template, times(1)).upload(any(), any(), any(), any()),
                    () -> assertThat(uploadUrl).isEqualTo(mockUrl.toString())
            );
        }
    }

    @Test
    @DisplayName("NCP Object Storage와의 통신 간 네트워크적인 오류가 발생한다")
    void throwExceptionByNCPCommunications() throws IOException {
        // given
        doThrow(StudyWithMeException.type(FileErrorCode.S3_UPLOAD_FAILURE))
                .when(s3Template)
                .upload(any(), any(), any(), any());

        final MultipartFile file = createSingleMockMultipartFile("hello3.pdf", "application/pdf");
        final RawFileData fileData = new RawFileData(file.getInputStream(), file.getContentType(), file.getOriginalFilename());

        // when - then
        assertThatThrownBy(() -> uploader.uploadWeeklySubmit(fileData))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(FileErrorCode.S3_UPLOAD_FAILURE.getMessage());

        verify(s3Template, times(1)).upload(any(), any(), any(), any());
    }

    private String createUploadLink(final String type, final String originalFileName) {
        return String.format(
                "https://kr.object.ncloudstorage.com/%s/%s/%s",
                BUCKET,
                type,
                UUID.randomUUID() + extractFileExtension(originalFileName)
        );
    }

    private String extractFileExtension(final String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
