package com.kgu.studywithme.file.domain.model;

import com.kgu.studywithme.file.exception.FileErrorCode;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.file.domain.model.FileUploadType.STUDY_DESCRIPTION_IMAGE;
import static com.kgu.studywithme.file.domain.model.FileUploadType.STUDY_WEEKLY_ASSIGNMENT;
import static com.kgu.studywithme.file.domain.model.FileUploadType.STUDY_WEEKLY_ATTACHMENT;
import static com.kgu.studywithme.file.domain.model.FileUploadType.STUDY_WEEKLY_CONTENT_IMAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("File -> 도메인 [FileUploadType] 테스트")
public class FileUploadTypeTest {
    @Test
    @DisplayName("유효하지 않은 UploadType으로 FileUploadType을 조회할 수 없다")
    void throwExceptionByInvalidUploadType() {
        assertThatThrownBy(() -> FileUploadType.from("anonymous"))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(FileErrorCode.INVALID_UPLOAD_TYPE.getMessage());
    }

    @Test
    @DisplayName("FileUploadType을 조회한다")
    void success() {
        assertAll(
                () -> assertThat(FileUploadType.from("studyDescriptionImage")).isEqualTo(STUDY_DESCRIPTION_IMAGE),
                () -> assertThat(FileUploadType.from("studyWeeklyContentImage")).isEqualTo(STUDY_WEEKLY_CONTENT_IMAGE),
                () -> assertThat(FileUploadType.from("studyWeeklyAttachment")).isEqualTo(STUDY_WEEKLY_ATTACHMENT),
                () -> assertThat(FileUploadType.from("studyWeeklyAssignment")).isEqualTo(STUDY_WEEKLY_ASSIGNMENT)
        );
    }
}
