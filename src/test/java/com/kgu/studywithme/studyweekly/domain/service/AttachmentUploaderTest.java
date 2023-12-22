package com.kgu.studywithme.studyweekly.domain.service;

import com.kgu.studywithme.common.mock.stub.StubFileUploader;
import com.kgu.studywithme.file.application.adapter.FileUploader;
import com.kgu.studywithme.file.domain.model.RawFileData;
import com.kgu.studywithme.file.utils.converter.FileConverter;
import com.kgu.studywithme.studyweekly.domain.model.UploadAttachment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.kgu.studywithme.common.utils.FileMockingUtils.createMultipleMockMultipartFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

/**
 * CompletableFuture asynchronous로 인해 단위 테스트에서 무한 대기하는 현상 발생 <br>
 * -> 임시 대안으로 @SpringBootTest + AttachmentUploader만 Context에 띄워서 진행
 */
@SpringBootTest(classes = {AttachmentUploader.class})
@Import(AttachmentUploaderTest.AttachmentUploaderTestConfiguration.class)
@DisplayName("StudyWeekly -> AttachmentUploader 테스트")
public class AttachmentUploaderTest {
    @TestConfiguration
    static class AttachmentUploaderTestConfiguration {
        @Bean
        public FileUploader fileUploader() {
            return new StubFileUploader();
        }

        @Bean
        public Executor fileUploadExecutor() {
            return Executors.newFixedThreadPool(10);
        }
    }

    @Autowired
    private AttachmentUploader sut;

    private final List<RawFileData> files = FileConverter.convertAttachmentFiles(List.of(
            createMultipleMockMultipartFile("hello1.txt", "text/plain"),
            createMultipleMockMultipartFile("hello3.pdf", "application/pdf")
    ));

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
