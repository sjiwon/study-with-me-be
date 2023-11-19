package com.kgu.studywithme.studyweekly.domain.service;

import com.kgu.studywithme.common.ParallelTest;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.studyattendance.domain.repository.StudyAttendanceRepository;
import com.kgu.studywithme.studyparticipant.domain.repository.StudyParticipantRepository;
import com.kgu.studywithme.studyweekly.domain.model.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.model.StudyWeeklyAttachment;
import com.kgu.studywithme.studyweekly.domain.repository.StudyWeeklyAttachmentRepository;
import com.kgu.studywithme.studyweekly.domain.repository.StudyWeeklyRepository;
import com.kgu.studywithme.studyweekly.domain.repository.StudyWeeklySubmitRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.kgu.studywithme.common.fixture.MemberFixture.ANONYMOUS;
import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_1;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_2;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.APPROVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyWeekly -> WeeklyManager 테스트")
public class WeeklyManagerTest extends ParallelTest {
    private final StudyWeeklyRepository studyWeeklyRepository = mock(StudyWeeklyRepository.class);
    private final StudyWeeklyAttachmentRepository studyWeeklyAttachmentRepository = mock(StudyWeeklyAttachmentRepository.class);
    private final StudyWeeklySubmitRepository studyWeeklySubmitRepository = mock(StudyWeeklySubmitRepository.class);
    private final StudyParticipantRepository studyParticipantRepository = mock(StudyParticipantRepository.class);
    private final StudyAttendanceRepository studyAttendanceRepository = mock(StudyAttendanceRepository.class);
    private final WeeklyManager sut = new WeeklyManager(
            studyWeeklyRepository,
            studyParticipantRepository,
            studyAttendanceRepository,
            studyWeeklyAttachmentRepository,
            studyWeeklySubmitRepository
    );

    private final Member host = JIWON.toMember().apply(1L);
    private final Member participantA = GHOST.toMember().apply(2L);
    private final Member participantB = ANONYMOUS.toMember().apply(3L);
    private final Study study = SPRING.toStudy(host).apply(1L);

    @Test
    @DisplayName("특정 주차를 생성한다")
    void saveWeekly() {
        // given
        final StudyWeekly target = STUDY_WEEKLY_1.toWeekly(study.getId(), host.getId());
        final StudyWeekly weekly = target.apply(1L);

        given(studyWeeklyRepository.save(any(StudyWeekly.class))).willReturn(weekly);
        given(studyParticipantRepository.findParticipantIdsByStatus(weekly.getStudyId(), APPROVE))
                .willReturn(List.of(host.getId(), participantA.getId(), participantB.getId()));

        // when
        final StudyWeekly savedWeekly = sut.saveWeekly(target);

        // then
        assertAll(
                () -> verify(studyWeeklyRepository, times(1)).save(any(StudyWeekly.class)),
                () -> verify(studyParticipantRepository, times(1)).findParticipantIdsByStatus(weekly.getStudyId(), APPROVE),
                () -> verify(studyAttendanceRepository, times(1)).saveAll(any()),
                () -> assertThat(savedWeekly.getId()).isEqualTo(weekly.getId())
        );
    }

    @Test
    @DisplayName("특정 주차 정보를 수정한다")
    void updateWeekly() {
        // given
        final StudyWeekly weekly = STUDY_WEEKLY_1.toWeekly(study.getId(), host.getId()).apply(1L);
        given(studyWeeklyRepository.getById(weekly.getId())).willReturn(weekly);

        // when
        sut.updateWeekly(
                weekly.getId(),
                STUDY_WEEKLY_2.getTitle(),
                STUDY_WEEKLY_2.getContent(),
                STUDY_WEEKLY_2.getPeriod().toPeriod(),
                STUDY_WEEKLY_2.isAssignmentExists(),
                STUDY_WEEKLY_2.isAutoAttendance(),
                STUDY_WEEKLY_2.getAttachments()
        );

        // then
        assertAll(
                () -> verify(studyWeeklyRepository, times(1)).getById(weekly.getId()),
                () -> assertThat(weekly.getTitle()).isEqualTo(STUDY_WEEKLY_2.getTitle()),
                () -> assertThat(weekly.getContent()).isEqualTo(STUDY_WEEKLY_2.getContent()),
                () -> assertThat(weekly.getPeriod().getStartDate()).isEqualTo(STUDY_WEEKLY_2.getPeriod().getStartDate()),
                () -> assertThat(weekly.getPeriod().getEndDate()).isEqualTo(STUDY_WEEKLY_2.getPeriod().getEndDate()),
                () -> assertThat(weekly.isAssignmentExists()).isEqualTo(STUDY_WEEKLY_2.isAssignmentExists()),
                () -> assertThat(weekly.isAutoAttendance()).isEqualTo(STUDY_WEEKLY_2.isAutoAttendance()),
                () -> assertThat(weekly.getAttachments())
                        .map(StudyWeeklyAttachment::getUploadAttachment)
                        .containsExactlyInAnyOrderElementsOf(STUDY_WEEKLY_2.getAttachments())
        );
    }

    @Test
    @DisplayName("특정 주차를 삭제한다")
    void deleteWeekly() {
        // given
        final StudyWeekly weekly = STUDY_WEEKLY_1.toWeekly(study.getId(), host.getId()).apply(1L);

        // when
        sut.deleteWeekly(weekly);

        // then
        assertAll(
                () -> verify(studyAttendanceRepository, times(1)).deleteFromSpecificWeekly(weekly.getStudyId(), weekly.getWeek()),
                () -> verify(studyWeeklySubmitRepository, times(1)).deleteFromSpecificWeekly(weekly.getId()),
                () -> verify(studyWeeklyAttachmentRepository, times(1)).deleteFromSpecificWeekly(weekly.getId()),
                () -> verify(studyWeeklyRepository, times(1)).deleteById(weekly.getId())
        );
    }
}
