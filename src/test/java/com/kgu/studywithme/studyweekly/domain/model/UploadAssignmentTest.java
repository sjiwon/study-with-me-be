package com.kgu.studywithme.studyweekly.domain.model;

import com.kgu.studywithme.common.ExecuteParallel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.studyweekly.domain.model.AssignmentSubmitType.FILE;
import static com.kgu.studywithme.studyweekly.domain.model.AssignmentSubmitType.LINK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExecuteParallel
@DisplayName("StudyWeekly/Submit -> 도메인 [UploadAssignment VO] 테스트")
class UploadAssignmentTest {
    @Test
    @DisplayName("Notion, Blog 등 링크를 통해서 과제를 제출한다")
    void constructWithLink() {
        final UploadAssignment uploadAssignment = UploadAssignment.withLink("https://notion.com");

        assertAll(
                () -> assertThat(uploadAssignment.getUploadFileName()).isNull(),
                () -> assertThat(uploadAssignment.getLink()).isEqualTo("https://notion.com"),
                () -> assertThat(uploadAssignment.getSubmitType()).isEqualTo(LINK)
        );
    }

    @Test
    @DisplayName("파일 업로드를 통해서 과제를 제출한다")
    void constructWithFile() {
        final UploadAssignment uploadAssignment = UploadAssignment.withFile("hello.pdf", "uuid.pdf");

        assertAll(
                () -> assertThat(uploadAssignment.getUploadFileName()).isEqualTo("hello.pdf"),
                () -> assertThat(uploadAssignment.getLink()).isEqualTo("uuid.pdf"),
                () -> assertThat(uploadAssignment.getSubmitType()).isEqualTo(FILE)
        );
    }
}
