package com.kgu.studywithme.studyweekly.domain.service;

import com.kgu.studywithme.common.ParallelTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.model.Score;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.studyattendance.domain.model.StudyAttendance;
import com.kgu.studywithme.studyattendance.domain.repository.StudyAttendanceRepository;
import com.kgu.studywithme.studyparticipant.domain.repository.query.ParticipateMemberReader;
import com.kgu.studywithme.studyparticipant.exception.StudyParticipantErrorCode;
import com.kgu.studywithme.studyweekly.domain.model.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.model.StudyWeeklySubmit;
import com.kgu.studywithme.studyweekly.domain.model.UploadAssignment;
import com.kgu.studywithme.studyweekly.domain.repository.StudyWeeklyRepository;
import com.kgu.studywithme.studyweekly.domain.repository.StudyWeeklySubmitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.PeriodFixture.WEEK_0;
import static com.kgu.studywithme.common.fixture.PeriodFixture.WEEK_1;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_1;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.ABSENCE;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.ATTENDANCE;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.LATE;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.NON_ATTENDANCE;
import static com.kgu.studywithme.studyweekly.domain.model.AssignmentSubmitType.LINK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyWeekly -> WeeklySubmitManager 테스트")
public class WeeklySubmitManagerTest extends ParallelTest {
    private final StudyWeeklyRepository studyWeeklyRepository = mock(StudyWeeklyRepository.class);
    private final ParticipateMemberReader participateMemberReader = fakeParticipateMemberReader();
    private final StudyWeeklySubmitRepository studyWeeklySubmitRepository = mock(StudyWeeklySubmitRepository.class);
    private final StudyAttendanceRepository studyAttendanceRepository = mock(StudyAttendanceRepository.class);
    private final WeeklySubmitManager sut = new WeeklySubmitManager(
            studyWeeklyRepository,
            participateMemberReader,
            studyWeeklySubmitRepository,
            studyAttendanceRepository
    );

    private Member host;
    private int previousScore;
    private Study study;
    private StudyWeekly manualAttendanceWeekly;
    private StudyWeekly autoAttendanceAndPreviousWeekly;
    private StudyWeekly autoAttendanceAndCurrentWeekly;

    @BeforeEach
    void setUp() {
        host = JIWON.toMember().apply(1L);
        previousScore = host.getScore().getValue();
        study = SPRING.toStudy(host).apply(1L);

        manualAttendanceWeekly = StudyWeekly.createWeeklyWithAssignment(
                study.getId(),
                host.getId(),
                STUDY_WEEKLY_1.getTitle(),
                STUDY_WEEKLY_1.getContent(),
                STUDY_WEEKLY_1.getWeek(),
                STUDY_WEEKLY_1.getPeriod().toPeriod(),
                false,
                STUDY_WEEKLY_1.getAttachments()
        ).apply(1L);
        autoAttendanceAndPreviousWeekly = StudyWeekly.createWeeklyWithAssignment(
                study.getId(),
                host.getId(),
                STUDY_WEEKLY_1.getTitle(),
                STUDY_WEEKLY_1.getContent(),
                STUDY_WEEKLY_1.getWeek(),
                WEEK_0.toPeriod(),
                true,
                STUDY_WEEKLY_1.getAttachments()
        ).apply(1L);
        autoAttendanceAndCurrentWeekly = StudyWeekly.createWeeklyWithAssignment(
                study.getId(),
                host.getId(),
                STUDY_WEEKLY_1.getTitle(),
                STUDY_WEEKLY_1.getContent(),
                STUDY_WEEKLY_1.getWeek(),
                WEEK_1.toPeriod(),
                true,
                STUDY_WEEKLY_1.getAttachments()
        ).apply(1L);
    }

    @Nested
    @DisplayName("과제 제출")
    class SubmitAssignment {
        private final UploadAssignment assignment = UploadAssignment.withLink("https://notions.so/hello");

        @Test
        @DisplayName("해당 Weekly -> 수동 출석")
        void manualAttendance() {
            // given
            given(studyWeeklyRepository.getById(manualAttendanceWeekly.getId())).willReturn(manualAttendanceWeekly);

            // when
            sut.submitAssignment(host.getId(), study.getId(), manualAttendanceWeekly.getId(), assignment);

            // then
            assertAll(
                    () -> verify(studyWeeklyRepository, times(1)).getById(manualAttendanceWeekly.getId()),
                    () -> verify(studyAttendanceRepository, times(0))
                            .getParticipantAttendanceByWeek(manualAttendanceWeekly.getStudyId(), host.getId(), manualAttendanceWeekly.getWeek()),
                    () -> assertThat(manualAttendanceWeekly.getSubmits()).hasSize(1),
                    () -> assertThat(manualAttendanceWeekly.getSubmits())
                            .map(StudyWeeklySubmit::getUploadAssignment)
                            .map(UploadAssignment::getSubmitType)
                            .containsExactlyInAnyOrder(LINK),
                    () -> assertThat(manualAttendanceWeekly.getSubmits())
                            .map(StudyWeeklySubmit::getUploadAssignment)
                            .map(UploadAssignment::getLink)
                            .containsExactlyInAnyOrder(assignment.getLink()),
                    () -> assertThat(host.getScore().getValue()).isEqualTo(previousScore)
            );
        }

        @Test
        @DisplayName("해당 Weekly -> 자동 출석 + 기간 안에 제출 O(출석)")
        void autoAttendanceCase1() {
            // given
            given(studyWeeklyRepository.getById(autoAttendanceAndCurrentWeekly.getId())).willReturn(autoAttendanceAndCurrentWeekly);

            final StudyAttendance attendance = StudyAttendance.recordAttendance(study, host, autoAttendanceAndCurrentWeekly.getWeek(), NON_ATTENDANCE).apply(1L);
            given(studyAttendanceRepository.getParticipantAttendanceByWeek(
                    autoAttendanceAndCurrentWeekly.getStudyId(),
                    host.getId(),
                    autoAttendanceAndCurrentWeekly.getWeek()
            )).willReturn(attendance);

            // when
            sut.submitAssignment(host.getId(), study.getId(), autoAttendanceAndCurrentWeekly.getId(), assignment);

            // then
            assertAll(
                    () -> verify(studyWeeklyRepository, times(1)).getById(autoAttendanceAndCurrentWeekly.getId()),
                    () -> verify(studyAttendanceRepository, times(1))
                            .getParticipantAttendanceByWeek(autoAttendanceAndCurrentWeekly.getStudyId(), host.getId(), autoAttendanceAndCurrentWeekly.getWeek()),
                    () -> assertThat(autoAttendanceAndCurrentWeekly.getSubmits()).hasSize(1),
                    () -> assertThat(autoAttendanceAndCurrentWeekly.getSubmits())
                            .map(StudyWeeklySubmit::getUploadAssignment)
                            .map(UploadAssignment::getSubmitType)
                            .containsExactlyInAnyOrder(LINK),
                    () -> assertThat(autoAttendanceAndCurrentWeekly.getSubmits())
                            .map(StudyWeeklySubmit::getUploadAssignment)
                            .map(UploadAssignment::getLink)
                            .containsExactlyInAnyOrder(assignment.getLink()),
                    () -> assertThat(host.getScore().getValue()).isEqualTo(previousScore + Score.ATTENDANCE),
                    () -> assertThat(attendance.getStatus()).isEqualTo(ATTENDANCE)
            );
        }

        @Test
        @DisplayName("해당 Weekly -> 자동 출석 + 기간 안에 제출 X [결석 -> 지각]")
        void autoAttendanceCase2() {
            // given
            given(studyWeeklyRepository.getById(autoAttendanceAndPreviousWeekly.getId())).willReturn(autoAttendanceAndPreviousWeekly);

            final StudyAttendance attendance = StudyAttendance.recordAttendance(study, host, autoAttendanceAndPreviousWeekly.getWeek(), ABSENCE).apply(1L);
            given(studyAttendanceRepository.getParticipantAttendanceByWeek(
                    autoAttendanceAndPreviousWeekly.getStudyId(),
                    host.getId(),
                    autoAttendanceAndPreviousWeekly.getWeek()
            )).willReturn(attendance);

            // when
            sut.submitAssignment(host.getId(), study.getId(), autoAttendanceAndPreviousWeekly.getId(), assignment);

            // then
            assertAll(
                    () -> verify(studyWeeklyRepository, times(1)).getById(autoAttendanceAndPreviousWeekly.getId()),
                    () -> verify(studyAttendanceRepository, times(1))
                            .getParticipantAttendanceByWeek(autoAttendanceAndPreviousWeekly.getStudyId(), host.getId(), autoAttendanceAndPreviousWeekly.getWeek()),
                    () -> assertThat(autoAttendanceAndPreviousWeekly.getSubmits()).hasSize(1),
                    () -> assertThat(autoAttendanceAndPreviousWeekly.getSubmits())
                            .map(StudyWeeklySubmit::getUploadAssignment)
                            .map(UploadAssignment::getSubmitType)
                            .containsExactlyInAnyOrder(LINK),
                    () -> assertThat(autoAttendanceAndPreviousWeekly.getSubmits())
                            .map(StudyWeeklySubmit::getUploadAssignment)
                            .map(UploadAssignment::getLink)
                            .containsExactlyInAnyOrder(assignment.getLink()),
                    () -> assertThat(host.getScore().getValue()).isEqualTo(previousScore - Score.ABSENCE + Score.LATE),
                    () -> assertThat(attendance.getStatus()).isEqualTo(LATE)
            );
        }

        @Test
        @DisplayName("해당 Weekly -> 자동 출석 + 기간 안에 제출 X [미출석 -> 지각]")
        void autoAttendanceCase3() {
            // given
            given(studyWeeklyRepository.getById(autoAttendanceAndPreviousWeekly.getId())).willReturn(autoAttendanceAndPreviousWeekly);

            final StudyAttendance attendance = StudyAttendance.recordAttendance(study, host, autoAttendanceAndPreviousWeekly.getWeek(), NON_ATTENDANCE).apply(1L);
            given(studyAttendanceRepository.getParticipantAttendanceByWeek(
                    autoAttendanceAndPreviousWeekly.getStudyId(),
                    host.getId(),
                    autoAttendanceAndPreviousWeekly.getWeek()
            )).willReturn(attendance);

            // when
            sut.submitAssignment(host.getId(), study.getId(), autoAttendanceAndPreviousWeekly.getId(), assignment);

            // then
            assertAll(
                    () -> verify(studyWeeklyRepository, times(1)).getById(autoAttendanceAndPreviousWeekly.getId()),
                    () -> verify(studyAttendanceRepository, times(1))
                            .getParticipantAttendanceByWeek(autoAttendanceAndPreviousWeekly.getStudyId(), host.getId(), autoAttendanceAndPreviousWeekly.getWeek()),
                    () -> assertThat(autoAttendanceAndPreviousWeekly.getSubmits()).hasSize(1),
                    () -> assertThat(autoAttendanceAndPreviousWeekly.getSubmits())
                            .map(StudyWeeklySubmit::getUploadAssignment)
                            .map(UploadAssignment::getSubmitType)
                            .containsExactlyInAnyOrder(LINK),
                    () -> assertThat(autoAttendanceAndPreviousWeekly.getSubmits())
                            .map(StudyWeeklySubmit::getUploadAssignment)
                            .map(UploadAssignment::getLink)
                            .containsExactlyInAnyOrder(assignment.getLink()),
                    () -> assertThat(host.getScore().getValue()).isEqualTo(previousScore + Score.LATE),
                    () -> assertThat(attendance.getStatus()).isEqualTo(LATE)
            );
        }
    }

    @Nested
    @DisplayName("제출한 과제 수정")
    class EditSubmittedAssignment {
        private final UploadAssignment defaultAssignment = UploadAssignment.withLink("https://notions.so/hello");
        private final UploadAssignment updateAssignment = UploadAssignment.withLink("https://notions.so/hello2");

        @Test
        @DisplayName("해당 Weekly -> 수동 출석")
        void manualAttendance() {
            // given
            final StudyWeeklySubmit submitted = StudyWeeklySubmit.submitAssignment(manualAttendanceWeekly, host.getId(), defaultAssignment).apply(1L);
            given(studyWeeklySubmitRepository.getSubmittedAssignment(manualAttendanceWeekly.getId(), host.getId())).willReturn(submitted);

            // when
            sut.editSubmittedAssignment(host.getId(), study.getId(), manualAttendanceWeekly.getId(), updateAssignment);

            // then
            assertAll(
                    () -> verify(studyWeeklySubmitRepository, times(1))
                            .getSubmittedAssignment(manualAttendanceWeekly.getId(), host.getId()),
                    () -> verify(studyAttendanceRepository, times(0))
                            .getParticipantAttendanceByWeek(manualAttendanceWeekly.getId(), host.getId(), manualAttendanceWeekly.getWeek()),
                    () -> assertThat(submitted.getUploadAssignment()).isEqualTo(updateAssignment),
                    () -> assertThat(host.getScore().getValue()).isEqualTo(previousScore)
            );
        }

        @Test
        @DisplayName("해당 Weekly -> 자동 출석 + 제출 기간 안에 수정 O")
        void autoAttendanceCase1() {
            // given
            final StudyWeeklySubmit submitted = StudyWeeklySubmit.submitAssignment(autoAttendanceAndCurrentWeekly, host.getId(), defaultAssignment).apply(1L);
            given(studyWeeklySubmitRepository.getSubmittedAssignment(autoAttendanceAndCurrentWeekly.getId(), host.getId())).willReturn(submitted);

            // when
            sut.editSubmittedAssignment(host.getId(), study.getId(), autoAttendanceAndCurrentWeekly.getId(), updateAssignment);

            // then
            assertAll(
                    () -> verify(studyWeeklySubmitRepository, times(1))
                            .getSubmittedAssignment(autoAttendanceAndCurrentWeekly.getId(), host.getId()),
                    () -> verify(studyAttendanceRepository, times(0))
                            .getParticipantAttendanceByWeek(autoAttendanceAndCurrentWeekly.getId(), host.getId(), autoAttendanceAndCurrentWeekly.getWeek()),
                    () -> assertThat(submitted.getUploadAssignment()).isEqualTo(updateAssignment),
                    () -> assertThat(host.getScore().getValue()).isEqualTo(previousScore)
            );
        }

        @Test
        @DisplayName("해당 Weekly -> 자동 출석 + 제출 기간 안에 수정 X + 이전 Status == ATTENDANCE")
        void autoAttendanceCase2() {
            // given
            final StudyWeeklySubmit submitted = StudyWeeklySubmit.submitAssignment(autoAttendanceAndPreviousWeekly, host.getId(), defaultAssignment).apply(1L);
            given(studyWeeklySubmitRepository.getSubmittedAssignment(autoAttendanceAndPreviousWeekly.getId(), host.getId())).willReturn(submitted);

            final StudyAttendance attendance = StudyAttendance.recordAttendance(study, host, autoAttendanceAndPreviousWeekly.getWeek(), ATTENDANCE).apply(1L);
            given(studyAttendanceRepository.getParticipantAttendanceByWeek(
                    autoAttendanceAndPreviousWeekly.getId(),
                    host.getId(),
                    autoAttendanceAndPreviousWeekly.getWeek()
            )).willReturn(attendance);

            // when
            sut.editSubmittedAssignment(host.getId(), study.getId(), autoAttendanceAndPreviousWeekly.getId(), updateAssignment);

            // then
            assertAll(
                    () -> verify(studyWeeklySubmitRepository, times(1))
                            .getSubmittedAssignment(autoAttendanceAndPreviousWeekly.getId(), host.getId()),
                    () -> verify(studyAttendanceRepository, times(1))
                            .getParticipantAttendanceByWeek(autoAttendanceAndPreviousWeekly.getId(), host.getId(), autoAttendanceAndPreviousWeekly.getWeek()),
                    () -> assertThat(submitted.getUploadAssignment()).isEqualTo(updateAssignment),
                    () -> assertThat(host.getScore().getValue()).isEqualTo(previousScore - Score.ATTENDANCE + Score.LATE),
                    () -> assertThat(attendance.getStatus()).isEqualTo(LATE)
            );
        }

        @Test
        @DisplayName("해당 Weekly -> 자동 출석 + 제출 기간 안에 수정 X + 이전 Status == LATE")
        void autoAttendanceCase3() {
            // given
            final StudyWeeklySubmit submitted = StudyWeeklySubmit.submitAssignment(autoAttendanceAndPreviousWeekly, host.getId(), defaultAssignment).apply(1L);
            given(studyWeeklySubmitRepository.getSubmittedAssignment(autoAttendanceAndPreviousWeekly.getId(), host.getId())).willReturn(submitted);

            final StudyAttendance attendance = StudyAttendance.recordAttendance(study, host, autoAttendanceAndPreviousWeekly.getWeek(), LATE).apply(1L);
            given(studyAttendanceRepository.getParticipantAttendanceByWeek(
                    autoAttendanceAndPreviousWeekly.getId(),
                    host.getId(),
                    autoAttendanceAndPreviousWeekly.getWeek()
            )).willReturn(attendance);

            // when
            sut.editSubmittedAssignment(host.getId(), study.getId(), autoAttendanceAndPreviousWeekly.getId(), updateAssignment);

            // then
            assertAll(
                    () -> verify(studyWeeklySubmitRepository, times(1))
                            .getSubmittedAssignment(autoAttendanceAndPreviousWeekly.getId(), host.getId()),
                    () -> verify(studyAttendanceRepository, times(1))
                            .getParticipantAttendanceByWeek(autoAttendanceAndPreviousWeekly.getId(), host.getId(), autoAttendanceAndPreviousWeekly.getWeek()),
                    () -> assertThat(submitted.getUploadAssignment()).isEqualTo(updateAssignment),
                    () -> assertThat(host.getScore().getValue()).isEqualTo(previousScore),
                    () -> assertThat(attendance.getStatus()).isEqualTo(LATE)
            );
        }
    }

    private ParticipateMemberReader fakeParticipateMemberReader() {
        return new ParticipateMemberReader() {
            @Override
            public Member getApplier(final Long studyId, final Long memberId) {
                throw StudyWithMeException.type(StudyParticipantErrorCode.APPLIER_NOT_FOUND);
            }

            @Override
            public Member getParticipant(final Long studyId, final Long memberId) {
                if (memberId.equals(host.getId())) {
                    return host;
                }

                throw StudyWithMeException.type(StudyParticipantErrorCode.PARTICIPANT_NOT_FOUND);
            }
        };
    }
}
