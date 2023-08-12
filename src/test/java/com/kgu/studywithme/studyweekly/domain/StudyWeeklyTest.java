package com.kgu.studywithme.studyweekly.domain;

import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.studyweekly.domain.attachment.StudyWeeklyAttachment;
import com.kgu.studywithme.studyweekly.domain.attachment.UploadAttachment;
import com.kgu.studywithme.studyweekly.domain.submit.StudyWeeklySubmit;
import com.kgu.studywithme.studyweekly.domain.submit.UploadAssignment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.PeriodFixture.WEEK_6;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.common.fixture.StudyWeeklyAttachmentFixture.HWPX_FILE;
import static com.kgu.studywithme.common.fixture.StudyWeeklyAttachmentFixture.IMG_FILE;
import static com.kgu.studywithme.common.fixture.StudyWeeklyAttachmentFixture.PDF_FILE;
import static com.kgu.studywithme.common.fixture.StudyWeeklyAttachmentFixture.TXT_FILE;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_1;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_5;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("StudyWeekly -> 도메인 [StudyWeekly] 테스트")
class StudyWeeklyTest {
    private final Member host = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final Member participant = JIWON.toMember().apply(2L, LocalDateTime.now());
    private final Study study = SPRING.toOnlineStudy(host.getId()).apply(1L, LocalDateTime.now());

    @Test
    @DisplayName("StudyWeekly를 생성한다")
    void construct() {
        final StudyWeekly weekly = STUDY_WEEKLY_5.toWeekly(study.getId(), host.getId());
        final StudyWeekly weeklyWithAssignment = STUDY_WEEKLY_1.toWeeklyWithAssignment(study.getId(), host.getId());

        assertAll(
                () -> assertThat(weekly.getStudyId()).isEqualTo(study.getId()),
                () -> assertThat(weekly.getCreatorId()).isEqualTo(host.getId()),
                () -> assertThat(weekly.getTitle()).isEqualTo(STUDY_WEEKLY_5.getTitle()),
                () -> assertThat(weekly.getContent()).isEqualTo(STUDY_WEEKLY_5.getContent()),
                () -> assertThat(weekly.getWeek()).isEqualTo(STUDY_WEEKLY_5.getWeek()),
                () -> assertThat(weekly.getPeriod().getStartDate()).isEqualTo(STUDY_WEEKLY_5.getPeriod().getStartDate()),
                () -> assertThat(weekly.getPeriod().getEndDate()).isEqualTo(STUDY_WEEKLY_5.getPeriod().getEndDate()),
                () -> assertThat(weekly.isAssignmentExists()).isFalse(),
                () -> assertThat(weekly.isAutoAttendance()).isFalse(),
                () -> assertThat(weekly.getAttachments())
                        .map(StudyWeeklyAttachment::getUploadAttachment)
                        .containsExactlyInAnyOrderElementsOf(STUDY_WEEKLY_5.getAttachments()),

                () -> assertThat(weeklyWithAssignment.getStudyId()).isEqualTo(study.getId()),
                () -> assertThat(weeklyWithAssignment.getCreatorId()).isEqualTo(host.getId()),
                () -> assertThat(weeklyWithAssignment.getTitle()).isEqualTo(STUDY_WEEKLY_1.getTitle()),
                () -> assertThat(weeklyWithAssignment.getContent()).isEqualTo(STUDY_WEEKLY_1.getContent()),
                () -> assertThat(weeklyWithAssignment.getWeek()).isEqualTo(STUDY_WEEKLY_1.getWeek()),
                () -> assertThat(weeklyWithAssignment.getPeriod().getStartDate()).isEqualTo(STUDY_WEEKLY_1.getPeriod().getStartDate()),
                () -> assertThat(weeklyWithAssignment.getPeriod().getEndDate()).isEqualTo(STUDY_WEEKLY_1.getPeriod().getEndDate()),
                () -> assertThat(weeklyWithAssignment.isAssignmentExists()).isTrue(),
                () -> assertThat(weeklyWithAssignment.isAutoAttendance()).isTrue(),
                () -> assertThat(weeklyWithAssignment.getAttachments())
                        .map(StudyWeeklyAttachment::getUploadAttachment)
                        .containsExactlyInAnyOrderElementsOf(STUDY_WEEKLY_1.getAttachments())
        );
    }

    @Test
    @DisplayName("StudyWeekly를 수정한다")
    void update() {
        // given
        final StudyWeekly weekly = STUDY_WEEKLY_5.toWeekly(study.getId(), host.getId());

        // when
        final List<UploadAttachment> attachments = List.of(
                new UploadAttachment(PDF_FILE.getUploadFileName(), PDF_FILE.getLink()),
                new UploadAttachment(TXT_FILE.getUploadFileName(), TXT_FILE.getLink()),
                new UploadAttachment(HWPX_FILE.getUploadFileName(), HWPX_FILE.getLink()),
                new UploadAttachment(IMG_FILE.getUploadFileName(), IMG_FILE.getLink())
        );
        weekly.update(
                "title",
                "content",
                WEEK_6.toPeriod(),
                true,
                true,
                attachments
        );

        // then
        assertAll(
                () -> assertThat(weekly.getTitle()).isEqualTo("title"),
                () -> assertThat(weekly.getContent()).isEqualTo("content"),
                () -> assertThat(weekly.getWeek()).isEqualTo(STUDY_WEEKLY_5.getWeek()),
                () -> assertThat(weekly.getPeriod().getStartDate()).isEqualTo(WEEK_6.getStartDate()),
                () -> assertThat(weekly.getPeriod().getEndDate()).isEqualTo(WEEK_6.getEndDate()),
                () -> assertThat(weekly.isAssignmentExists()).isTrue(),
                () -> assertThat(weekly.isAutoAttendance()).isTrue(),
                () -> assertThat(weekly.getAttachments())
                        .map(StudyWeeklyAttachment::getUploadAttachment)
                        .containsExactlyInAnyOrderElementsOf(attachments)
        );
    }

    @Test
    @DisplayName("과제가 존재하는 Weekly에 과제를 제출한다")
    void submitAssignment() {
        // given
        final StudyWeekly weekly = STUDY_WEEKLY_1.toWeeklyWithAssignment(study.getId(), host.getId());

        // when
        final UploadAssignment hostUploadAssignment = UploadAssignment.withLink("https://google.com");
        final UploadAssignment participantUploadAssignment = UploadAssignment.withLink("https://naver.com");
        weekly.submitAssignment(host.getId(), hostUploadAssignment);
        weekly.submitAssignment(participant.getId(), participantUploadAssignment);

        // then
        assertAll(
                () -> assertThat(weekly.getSubmits()).hasSize(2),
                () -> assertThat(weekly.getSubmits())
                        .map(StudyWeeklySubmit::getParticipantId)
                        .containsExactlyInAnyOrder(host.getId(), participant.getId()),
                () -> assertThat(weekly.getSubmits())
                        .map(StudyWeeklySubmit::getUploadAssignment)
                        .containsExactlyInAnyOrder(hostUploadAssignment, participantUploadAssignment)
        );
    }
}
