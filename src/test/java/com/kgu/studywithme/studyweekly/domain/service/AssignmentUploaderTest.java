package com.kgu.studywithme.studyweekly.domain.service;

import com.kgu.studywithme.common.ParallelTest;
import com.kgu.studywithme.common.mock.stub.StubFileUploader;
import com.kgu.studywithme.file.application.adapter.FileUploader;
import com.kgu.studywithme.file.domain.model.RawFileData;
import com.kgu.studywithme.file.utils.converter.FileConverter;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studyweekly.domain.model.UploadAssignment;
import com.kgu.studywithme.studyweekly.exception.StudyWeeklyErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.common.utils.FileMockingUtils.createSingleMockMultipartFile;
import static com.kgu.studywithme.studyweekly.domain.model.AssignmentSubmitType.FILE;
import static com.kgu.studywithme.studyweekly.domain.model.AssignmentSubmitType.LINK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("StudyWeekly -> AssignmentUploader 테스트")
public class AssignmentUploaderTest extends ParallelTest {
    private final FileUploader fileUploader = new StubFileUploader();
    private final AssignmentUploader sut = new AssignmentUploader(fileUploader);

    private final RawFileData file = FileConverter.convertAssignmentFile(createSingleMockMultipartFile("hello3.pdf", "application/pdf"));
    private final String link = "https://notion.so/assignment";

    @Test
    @DisplayName("과제 제출물은 링크 또는 파일 중 하나를 반드시 업로드해야 하고 그러지 않으면 제출이 불가능하다")
    void throwExceptionByMissingSubmission() {
        assertThatThrownBy(() -> sut.uploadAssignment(LINK, null, null))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyWeeklyErrorCode.MISSING_SUBMISSION.getMessage());
    }

    @Test
    @DisplayName("과제 제출물은 링크 또는 파일 중 한가지만 업로드해야 하고 그러지 않으면 제출이 불가능하다")
    void throwExceptionByDuplicateSubmission() {
        assertThatThrownBy(() -> sut.uploadAssignment(LINK, file, link))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyWeeklyErrorCode.DUPLICATE_SUBMISSION.getMessage());
    }

    @Test
    @DisplayName("제출한 타입[link/file]에 대해서 실제 제출한 양식[링크/파일]이 매칭이 되지 않음에 따라 제출이 불가능하다")
    void throwExceptionByInvalidBetweenSubmitTypAndResult() {
        assertAll(
                () -> assertThatThrownBy(() -> sut.uploadAssignment(LINK, file, null))
                        .isInstanceOf(StudyWithMeException.class)
                        .hasMessage(StudyWeeklyErrorCode.INVALID_BETWEEN_SUBMIT_TYPE_AND_RESULT.getMessage()),
                () -> assertThatThrownBy(() -> sut.uploadAssignment(FILE, null, link))
                        .isInstanceOf(StudyWithMeException.class)
                        .hasMessage(StudyWeeklyErrorCode.INVALID_BETWEEN_SUBMIT_TYPE_AND_RESULT.getMessage())
        );
    }

    @Test
    @DisplayName("과제를 제출한다 [with Link]")
    void successWithLink() {
        // when
        final UploadAssignment assignment = sut.uploadAssignment(LINK, null, link);

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
        // when
        final UploadAssignment assignment = sut.uploadAssignment(FILE, file, null);

        // then
        assertAll(
                () -> assertThat(assignment.getSubmitType()).isEqualTo(FILE),
                () -> assertThat(assignment.getUploadFileName()).isEqualTo(file.fileName()),
                () -> assertThat(assignment.getLink()).isEqualTo("S3/" + file.fileName())
        );
    }
}
