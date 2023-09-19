package com.kgu.studywithme.studyweekly.domain.model;

import com.kgu.studywithme.common.ParallelTest;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.domain.model.Study;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_1;
import static com.kgu.studywithme.studyweekly.domain.model.AssignmentSubmitType.FILE;
import static com.kgu.studywithme.studyweekly.domain.model.AssignmentSubmitType.LINK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("StudyWeekly/Submit -> 도메인 [StudyWeeklySubmit] 테스트")
class StudyWeeklySubmitTest extends ParallelTest {
    private final Member host = JIWON.toMember().apply(1L);
    private final Member participant = JIWON.toMember().apply(2L);
    private final Study study = SPRING.toOnlineStudy(host.getId()).apply(1L);
    private final StudyWeekly weekly = STUDY_WEEKLY_1.toWeeklyWithAssignment(study.getId(), host.getId())
            .apply(1L);

    @Test
    @DisplayName("StudyWeeklySubmit[With Link]을 생성한다")
    void constructWithLink() {
        final UploadAssignment uploadAssignment = UploadAssignment.withLink("https://notion.com");
        final StudyWeeklySubmit submit = StudyWeeklySubmit.submitAssignment(weekly, participant.getId(), uploadAssignment);

        assertAll(
                () -> assertThat(submit.getWeekly()).isEqualTo(weekly),
                () -> assertThat(submit.getUploadAssignment().getUploadFileName()).isNull(),
                () -> assertThat(submit.getUploadAssignment().getLink()).isEqualTo("https://notion.com"),
                () -> assertThat(submit.getUploadAssignment().getSubmitType()).isEqualTo(LINK)
        );
    }

    @Test
    @DisplayName("StudyWeeklySubmit[With File]을 생성한다")
    void constructWithFile() {
        final UploadAssignment uploadAssignment = UploadAssignment.withFile("hello.pdf", "uuid.pdf");
        final StudyWeeklySubmit submit = StudyWeeklySubmit.submitAssignment(weekly, participant.getId(), uploadAssignment);

        assertAll(
                () -> assertThat(submit.getWeekly()).isEqualTo(weekly),
                () -> assertThat(submit.getUploadAssignment().getUploadFileName()).isEqualTo("hello.pdf"),
                () -> assertThat(submit.getUploadAssignment().getLink()).isEqualTo("uuid.pdf"),
                () -> assertThat(submit.getUploadAssignment().getSubmitType()).isEqualTo(FILE)
        );
    }

    @Test
    @DisplayName("업로드한 과제를 수정한다")
    void editUpload() {
        // given
        final UploadAssignment uploadAssignment = UploadAssignment.withFile("hello.pdf", "uuid.pdf");
        final StudyWeeklySubmit submit = StudyWeeklySubmit.submitAssignment(weekly, participant.getId(), uploadAssignment);

        // when
        final UploadAssignment newUploadAssignment = UploadAssignment.withLink("https://notion.so");
        submit.editUpload(newUploadAssignment);

        // then
        assertAll(
                () -> assertThat(submit.getWeekly()).isEqualTo(weekly),
                () -> assertThat(submit.getUploadAssignment().getUploadFileName()).isNull(),
                () -> assertThat(submit.getUploadAssignment().getLink()).isEqualTo(newUploadAssignment.getLink()),
                () -> assertThat(submit.getUploadAssignment().getSubmitType()).isEqualTo(newUploadAssignment.getSubmitType())
        );
    }
}
