package com.kgu.studywithme.studyweekly.domain.model;

import com.kgu.studywithme.common.ParallelTest;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.domain.model.Study;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("StudyWeekly/Attachment -> 도메인 [StudyWeeklyAttachment] 테스트")
class StudyWeeklyAttachmentTest extends ParallelTest {
    private final Member host = JIWON.toMember().apply(1L);
    private final Study study = SPRING.toOnlineStudy(host.getId()).apply(1L);
    private final StudyWeekly weekly = STUDY_WEEKLY_1.toWeeklyWithAssignment(study.getId(), host.getId()).apply(1L);

    @Test
    @DisplayName("StudyWeeklyAttachment를 생성한다")
    void construct() {
        final UploadAttachment uploadAttachment = new UploadAttachment("hello.pdf", "uuid.pdf");
        final StudyWeeklyAttachment attachment = StudyWeeklyAttachment.addAttachmentFile(weekly, uploadAttachment);

        assertAll(
                () -> assertThat(attachment.getWeekly()).isEqualTo(weekly),
                () -> assertThat(attachment.getUploadAttachment().getUploadFileName()).isEqualTo("hello.pdf"),
                () -> assertThat(attachment.getUploadAttachment().getLink()).isEqualTo("uuid.pdf")
        );
    }
}
