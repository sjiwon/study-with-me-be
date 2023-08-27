package com.kgu.studywithme.studyweekly.domain.attachment;

import com.kgu.studywithme.common.ExecuteParallel;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.studyweekly.domain.StudyWeekly;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExecuteParallel
@DisplayName("StudyWeekly/Attachment -> 도메인 [StudyWeeklyAttachment] 테스트")
class StudyWeeklyAttachmentTest {
    private final Member host = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final Study study = SPRING.toOnlineStudy(host.getId()).apply(1L, LocalDateTime.now());
    private final StudyWeekly weekly = STUDY_WEEKLY_1.toWeeklyWithAssignment(study.getId(), host.getId())
            .apply(1L, LocalDateTime.now());

    @Test
    @DisplayName("StudyWeeklyAttachment를 생성한다")
    void construct() {
        final UploadAttachment uploadAttachment = new UploadAttachment("hello.pdf", "uuid.pdf");
        final StudyWeeklyAttachment attachment = StudyWeeklyAttachment.addAttachmentFile(weekly, uploadAttachment);

        assertAll(
                () -> assertThat(attachment.getStudyWeekly()).isEqualTo(weekly),
                () -> assertThat(attachment.getUploadAttachment().getUploadFileName()).isEqualTo("hello.pdf"),
                () -> assertThat(attachment.getUploadAttachment().getLink()).isEqualTo("uuid.pdf")
        );
    }
}
