package com.kgu.studywithme.studyweekly.domain.service;

import com.kgu.studywithme.common.ParallelTest;
import com.kgu.studywithme.common.mock.stub.StubFileUploader;
import com.kgu.studywithme.file.application.adapter.FileUploader;
import com.kgu.studywithme.file.domain.model.RawFileData;
import com.kgu.studywithme.file.utils.converter.FileConverter;
import com.kgu.studywithme.studyweekly.domain.model.UploadAttachment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static com.kgu.studywithme.common.utils.FileMockingUtils.createMultipleMockMultipartFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("StudyWeekly -> AttachmentUploader 테스트")
public class AttachmentUploaderTest extends ParallelTest {
    private final FileUploader fileUploader = new StubFileUploader();
    private final AttachmentUploader sut = new AttachmentUploader(fileUploader);

    private List<RawFileData> files;

    @BeforeEach
    void setUp() throws IOException {
        files = FileConverter.convertAttachmentFiles(List.of(
                createMultipleMockMultipartFile("hello1.txt", "text/plain"),
                createMultipleMockMultipartFile("hello3.pdf", "application/pdf")
        ));
    }

    @Test
    @DisplayName("첨부파일을 업로드 하지 않으면 EmptyList가 반환된다")
    void emptyAttachment() {
        // when
        final List<UploadAttachment> attachments = sut.uploadAttachments(List.of());

        // then
        assertThat(attachments).isEmpty();
    }

    @Test
    @DisplayName("첨부파일을 업로드한다")
    void success() {
        // when
        final List<UploadAttachment> attachments = sut.uploadAttachments(files);

        // then
        assertAll(
                () -> assertThat(attachments).hasSize(2),
                () -> assertThat(attachments)
                        .map(UploadAttachment::getUploadFileName)
                        .containsExactlyInAnyOrder("hello1.txt", "hello3.pdf"),
                () -> assertThat(attachments)
                        .map(UploadAttachment::getLink)
                        .containsExactlyInAnyOrder("S3/hello1.txt", "S3/hello3.pdf")
        );
    }
}
