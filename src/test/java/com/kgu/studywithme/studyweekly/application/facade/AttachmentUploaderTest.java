package com.kgu.studywithme.studyweekly.application.facade;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.file.application.adapter.FileUploader;
import com.kgu.studywithme.studyweekly.domain.attachment.UploadAttachment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.kgu.studywithme.common.utils.FileMockingUtils.createMultipleMockMultipartFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

@DisplayName("StudyWeekly -> AttachmentUploader 테스트")
public class AttachmentUploaderTest extends UseCaseTest {
    @InjectMocks
    private AttachmentUploader attachmentUploader;

    @Mock
    private FileUploader fileUploader;

    private MultipartFile file1;
    private MultipartFile file2;
    private MultipartFile file3;
    private MultipartFile file4;
    private List<MultipartFile> files;

    @BeforeEach
    void setUp() throws IOException {
        file1 = createMultipleMockMultipartFile("hello1.txt", "text/plain");
        file2 = createMultipleMockMultipartFile("hello2.hwpx", "application/x-hwpml");
        file3 = createMultipleMockMultipartFile("hello3.pdf", "application/pdf");
        file4 = createMultipleMockMultipartFile("hello4.png", "image/png");
        files = List.of(file1, file2, file3, file4);
    }

    @Test
    @DisplayName("첨부파일을 업로드 하지 않으면 EmptyList가 반환된다")
    void emptyAttachment() {
        // when
        final List<UploadAttachment> attachments = attachmentUploader.uploadAttachments(List.of());

        // then
        assertThat(attachments).hasSize(0);
    }

    @Test
    @DisplayName("첨부파일을 업로드한다")
    void success() {
        // given
        given(fileUploader.uploadWeeklyAttachment(file1)).willReturn("AWS/hello1.txt");
        given(fileUploader.uploadWeeklyAttachment(file2)).willReturn("AWS/hello2.hwpx");
        given(fileUploader.uploadWeeklyAttachment(file3)).willReturn("AWS/hello3.pdf");
        given(fileUploader.uploadWeeklyAttachment(file4)).willReturn("AWS/hello4.png");

        // when
        final List<UploadAttachment> attachments = attachmentUploader.uploadAttachments(files);

        // then
        assertAll(
                () -> assertThat(attachments).hasSize(4),
                () -> assertThat(attachments)
                        .map(UploadAttachment::getUploadFileName)
                        .containsExactlyInAnyOrder("hello1.txt", "hello2.hwpx", "hello3.pdf", "hello4.png"),
                () -> assertThat(attachments)
                        .map(UploadAttachment::getLink)
                        .containsExactlyInAnyOrder(
                                "AWS/hello1.txt",
                                "AWS/hello2.hwpx",
                                "AWS/hello3.pdf",
                                "AWS/hello4.png"
                        )
        );
    }
}
