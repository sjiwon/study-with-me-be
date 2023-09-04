package com.kgu.studywithme.studyweekly.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.file.application.adapter.FileUploader;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studyweekly.domain.submit.UploadAssignment;
import com.kgu.studywithme.studyweekly.exception.StudyWeeklyErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.kgu.studywithme.common.utils.FileMockingUtils.createSingleMockMultipartFile;
import static com.kgu.studywithme.studyweekly.domain.submit.AssignmentSubmitType.FILE;
import static com.kgu.studywithme.studyweekly.domain.submit.AssignmentSubmitType.LINK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@DisplayName("StudyWeekly -> AssignmentUploader 테스트")
public class AssignmentUploaderTest extends UseCaseTest {
    @InjectMocks
    private AssignmentUploader assignmentUploader;

    @Mock
    private FileUploader fileUploader;

    private MultipartFile file;
    private String link;

    @BeforeEach
    void setUp() throws IOException {
        file = createSingleMockMultipartFile("hello3.pdf", "application/pdf");
        link = "https://notion.so/my-assignment";
    }

    @Test
    @DisplayName("과제 제출물은 링크 또는 파일 중 하나를 반드시 업로드해야 하고 그러지 않으면 제출이 불가능하다")
    void throwExceptionByMissingSubmission() {
        assertThatThrownBy(() -> assignmentUploader.uploadAssignmentWithFile(LINK, null, null))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyWeeklyErrorCode.MISSING_SUBMISSION.getMessage());
    }

    @Test
    @DisplayName("과제 제출물은 링크 또는 파일 중 한가지만 업로드해야 하고 그러지 않으면 제출이 불가능하다")
    void throwExceptionByDuplicateSubmission() {
        assertThatThrownBy(() -> assignmentUploader.uploadAssignmentWithFile(LINK, file, link))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyWeeklyErrorCode.DUPLICATE_SUBMISSION.getMessage());
    }

    @Test
    @DisplayName("제출한 타입[link/file]에 대해서 실제 제출한 양식[링크/파일]이 매칭이 되지 않음에 따라 제출이 불가능하다")
    void throwExceptionByInvalidBetweenSubmitTypAndResult() {
        assertAll(
                () -> assertThatThrownBy(() -> assignmentUploader.uploadAssignmentWithFile(LINK, file, null))
                        .isInstanceOf(StudyWithMeException.class)
                        .hasMessage(StudyWeeklyErrorCode.INVALID_BETWEEN_SUBMIT_TYPE_AND_RESULT.getMessage()),
                () -> assertThatThrownBy(() -> assignmentUploader.uploadAssignmentWithFile(FILE, null, link))
                        .isInstanceOf(StudyWithMeException.class)
                        .hasMessage(StudyWeeklyErrorCode.INVALID_BETWEEN_SUBMIT_TYPE_AND_RESULT.getMessage())
        );
    }

    @Test
    @DisplayName("과제를 제출한다 [with Link]")
    void successWithLink() {
        // when
        final UploadAssignment assignment = assignmentUploader.uploadAssignmentWithFile(LINK, null, link);

        // then
        assertAll(
                () -> assertThat(assignment.getSubmitType()).isEqualTo(LINK),
                () -> assertThat(assignment.getUploadFileName()).isNull(),
                () -> assertThat(assignment.getLink()).isEqualTo(link)
        );
    }

    @Test
    @DisplayName("과제를 제출한다 [with File]")
    void successWithFile() {
        // given
        given(fileUploader.uploadWeeklySubmit(any())).willReturn("AWS/hello3.pdf");

        // when
        final UploadAssignment assignment = assignmentUploader.uploadAssignmentWithFile(FILE, file, null);

        // then
        assertAll(
                () -> assertThat(assignment.getSubmitType()).isEqualTo(FILE),
                () -> assertThat(assignment.getUploadFileName()).isEqualTo("hello3.pdf"),
                () -> assertThat(assignment.getLink()).isEqualTo("AWS/hello3.pdf")
        );
    }
}
