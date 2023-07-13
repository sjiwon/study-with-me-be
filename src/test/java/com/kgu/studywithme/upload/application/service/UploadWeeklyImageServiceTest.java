package com.kgu.studywithme.upload.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.upload.application.usecase.command.UploadWeeklyImageUseCase;
import com.kgu.studywithme.upload.utils.FileUploader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.kgu.studywithme.common.utils.FileMockingUtils.createSingleMockMultipartFile;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Upload -> UploadWeeklyImageService 테스트")
class UploadWeeklyImageServiceTest extends UseCaseTest {
    @InjectMocks
    private UploadWeeklyImageService uploadWeeklyImageService;

    @Mock
    private FileUploader fileUploader;

    @Test
    @DisplayName("주차별 글 내부 이미지를 업로드한다")
    void success() throws IOException {
        // given
        final MultipartFile file = createSingleMockMultipartFile("hello4.png", "image/png");

        // when
        uploadWeeklyImageService.upload(new UploadWeeklyImageUseCase.Command(file));

        // then
        verify(fileUploader, times(1)).uploadWeeklyImage(file);
    }
}
