package com.kgu.studywithme.file.infrastructure.s3;

import com.kgu.studywithme.common.ParallelTest;
import com.kgu.studywithme.file.domain.model.RawFileData;
import com.kgu.studywithme.file.exception.FileErrorCode;
import com.kgu.studywithme.file.utils.converter.FileConverter;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
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
class S3FileUploaderTest extends ParallelTest {
    private static final String BUCKET = "bucket";
    private static final String CLOUD_FRONT_URL = "https://cloudfront-domain";

    private final S3Template s3Template = mock(S3Template.class);
    private final S3Resource s3Resource = mock(S3Resource.class);
    private final S3FileUploader sut = new S3FileUploader(s3Template, BUCKET, CLOUD_FRONT_URL);

    private final RawFileData rawFileData = FileConverter.convertImageFile(
            createSingleMockMultipartFile("hello4.png", "image/png"),
            STUDY_DESCRIPTION_IMAGE
    );
    private final String imageUploadLinkPath = "/" + rawFileData.fileName();

    @Test
    @DisplayName("Client가 파일을 전송하지 않았으면 예외가 발생한다")
    void throwExceptionByFileIsNotUpload() {
        assertThatThrownBy(() -> sut.uploadFile(null))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(FileErrorCode.FILE_IS_NOT_UPLOAD.getMessage());

        verify(s3Template, times(0)).upload(any(String.class), any(String.class), any(InputStream.class), any(ObjectMetadata.class));
    }

    @Test
    @DisplayName("S3에 파일을 업로드한다")
    void success() throws IOException {
        // given
        final URL mockUrl = new URL("https://s3" + imageUploadLinkPath);
        given(s3Template.upload(
                any(String.class),
                any(String.class),
                any(InputStream.class),
                any(ObjectMetadata.class)
        )).willReturn(s3Resource);
        given(s3Resource.getURL()).willReturn(mockUrl);

        // when
        final String uploadLink = sut.uploadFile(rawFileData);

        // then
        assertAll(
                () -> verify(s3Template, times(1)).upload(any(String.class), any(String.class), any(InputStream.class), any(ObjectMetadata.class)),
                () -> assertThat(uploadLink).isEqualTo(CLOUD_FRONT_URL + imageUploadLinkPath)
        );
    }
}
