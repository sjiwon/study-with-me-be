package com.kgu.studywithme.file.infrastructure.s3;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.file.domain.model.RawFileData;
import com.kgu.studywithme.file.exception.FileErrorCode;
import com.kgu.studywithme.file.utils.converter.FileConverter;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;

import static com.kgu.studywithme.common.utils.FileMockingUtils.createSingleMockMultipartFile;
import static com.kgu.studywithme.file.domain.model.FileUploadType.STUDY_DESCRIPTION_IMAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("File -> S3FileUploader 테스트")
class S3FileUploaderTest extends UseCaseTest {
    private static final String BUCKET = "bucket";
    private static final String CLOUD_FRONT_URL = "https://cloudfront-domain";

    private final S3Template s3Template = mock(S3Template.class);
    private final S3Resource s3Resource = mock(S3Resource.class);
    private final S3FileUploader sut = new S3FileUploader(s3Template, BUCKET, CLOUD_FRONT_URL);

    private RawFileData rawFileData;
    private String imageUploadLinkPath;

    @BeforeEach
    void setUp() throws IOException {
        final MultipartFile file = createSingleMockMultipartFile("hello4.png", "image/png");
        rawFileData = FileConverter.convertImageFile(file, STUDY_DESCRIPTION_IMAGE);
        imageUploadLinkPath = "/" + rawFileData.fileName();
    }

    @Test
    @DisplayName("파일을 업로드하지 않으면 예외가 발생한다")
    void throwExceptionByFileIsNotUpload() {
        assertThatThrownBy(() -> sut.uploadFile(null))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(FileErrorCode.FILE_IS_NOT_UPLOAD.getMessage());

        verify(s3Template, times(0)).upload(any(), any(), any(), any());
    }

    @Test
    @DisplayName("S3에 파일을 업로드한다")
    void success() throws IOException {
        // given
        final URL mockUrl = new URL("https://s3" + imageUploadLinkPath);
        given(s3Template.upload(any(), any(), any(), any())).willReturn(s3Resource);
        given(s3Resource.getURL()).willReturn(mockUrl);

        // when
        final String uploadLink = sut.uploadFile(rawFileData);

        // then
        assertAll(
                () -> verify(s3Template, times(1)).upload(any(), any(), any(), any()),
                () -> assertThat(uploadLink).isEqualTo(CLOUD_FRONT_URL + imageUploadLinkPath)
        );
    }
}
