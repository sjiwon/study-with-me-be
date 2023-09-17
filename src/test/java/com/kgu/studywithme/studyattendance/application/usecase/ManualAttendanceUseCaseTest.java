package com.kgu.studywithme.studyattendance.application.usecase;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.model.Score;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.studyattendance.application.usecase.command.ManualAttendanceCommand;
import com.kgu.studywithme.studyattendance.domain.model.StudyAttendance;
import com.kgu.studywithme.studyattendance.domain.repository.StudyAttendanceRepository;
import com.kgu.studywithme.studyattendance.domain.service.ParticipantScoreUpdateProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.ABSENCE;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.ATTENDANCE;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.LATE;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.NON_ATTENDANCE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyAttendance -> ManualAttendanceUseCase 테스트")
class ManualAttendanceUseCaseTest extends UseCaseTest {
    private final StudyAttendanceRepository studyAttendanceRepository = mock(StudyAttendanceRepository.class);
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final ParticipantScoreUpdateProcessor participantScoreUpdateProcessor = new ParticipantScoreUpdateProcessor(memberRepository);
    private final ManualAttendanceUseCase sut = new ManualAttendanceUseCase(studyAttendanceRepository, participantScoreUpdateProcessor);

    private Member member;
    private int previousScore;
    private Study study;

    @BeforeEach
    void setUp() {
        member = JIWON.toMember().apply(1L);
        previousScore = member.getScore().getValue();
        study = SPRING.toOnlineStudy(member.getId()).apply(1L);

        given(memberRepository.getById(member.getId())).willReturn(member);
    }

    @Test
    @DisplayName("수동 출석 체크를 진행한다 [미출석 -> 출석]")
    void nonAttendanceToAttendance() {
        // given
        final StudyAttendance attendance = StudyAttendance.recordAttendance(study.getId(), member.getId(), 1, NON_ATTENDANCE);
        final ManualAttendanceCommand command = new ManualAttendanceCommand(study.getId(), member.getId(), 1, ATTENDANCE);
        given(studyAttendanceRepository.getParticipantAttendanceByWeek(command.studyId(), command.participantId(), command.week()))
                .willReturn(Optional.of(attendance));

        // when
        sut.invoke(command);

        // then
        assertAll(
                () -> verify(studyAttendanceRepository, times(1))
                        .getParticipantAttendanceByWeek(command.studyId(), command.participantId(), command.week()),
                () -> verify(memberRepository, times(1)).getById(command.participantId()),
                () -> assertThat(attendance.getStatus()).isEqualTo(ATTENDANCE),
                () -> assertThat(member.getScore().getValue()).isEqualTo(previousScore + Score.ATTENDANCE)
        );
    }

    @Test
    @DisplayName("수동 출석 체크를 진행한다 [미출석 -> 지각]")
    void nonAttendanceToLate() {
        // given
        final StudyAttendance attendance = StudyAttendance.recordAttendance(study.getId(), member.getId(), 1, NON_ATTENDANCE);
        final ManualAttendanceCommand command = new ManualAttendanceCommand(study.getId(), member.getId(), 1, LATE);
        given(studyAttendanceRepository.getParticipantAttendanceByWeek(command.studyId(), command.participantId(), command.week()))
                .willReturn(Optional.of(attendance));

        // when
        sut.invoke(command);

        // then
        assertAll(
                () -> verify(studyAttendanceRepository, times(1))
                        .getParticipantAttendanceByWeek(command.studyId(), command.participantId(), command.week()),
                () -> verify(memberRepository, times(1)).getById(command.participantId()),
                () -> assertThat(attendance.getStatus()).isEqualTo(LATE),
                () -> assertThat(member.getScore().getValue()).isEqualTo(previousScore + Score.LATE)
        );
    }

    @Test
    @DisplayName("수동 출석 체크를 진행한다 [미출석 -> 결석]")
    void nonAttendanceToAbsence() {
        // given
        final StudyAttendance attendance = StudyAttendance.recordAttendance(study.getId(), member.getId(), 1, NON_ATTENDANCE);
        final ManualAttendanceCommand command = new ManualAttendanceCommand(study.getId(), member.getId(), 1, ABSENCE);
        given(studyAttendanceRepository.getParticipantAttendanceByWeek(command.studyId(), command.participantId(), command.week()))
                .willReturn(Optional.of(attendance));

        // when
        sut.invoke(command);

        // then
        assertAll(
                () -> verify(studyAttendanceRepository, times(1))
                        .getParticipantAttendanceByWeek(command.studyId(), command.participantId(), command.week()),
                () -> verify(memberRepository, times(1)).getById(command.participantId()),
                () -> assertThat(attendance.getStatus()).isEqualTo(ABSENCE),
                () -> assertThat(member.getScore().getValue()).isEqualTo(previousScore + Score.ABSENCE)
        );
    }

    @Test
    @DisplayName("수동 출석 체크를 진행한다 [출석 -> 지각]")
    void attendanceToLate() {
        // given
        final StudyAttendance attendance = StudyAttendance.recordAttendance(study.getId(), member.getId(), 1, ATTENDANCE);
        final ManualAttendanceCommand command = new ManualAttendanceCommand(study.getId(), member.getId(), 1, LATE);
        given(studyAttendanceRepository.getParticipantAttendanceByWeek(command.studyId(), command.participantId(), command.week()))
                .willReturn(Optional.of(attendance));

        // when
        sut.invoke(command);

        // then
        assertAll(
                () -> verify(studyAttendanceRepository, times(1))
                        .getParticipantAttendanceByWeek(command.studyId(), command.participantId(), command.week()),
                () -> verify(memberRepository, times(1)).getById(command.participantId()),
                () -> assertThat(attendance.getStatus()).isEqualTo(LATE),
                () -> assertThat(member.getScore().getValue()).isEqualTo(previousScore - Score.ATTENDANCE + Score.LATE)
        );
    }

    @Test
    @DisplayName("수동 출석 체크를 진행한다 [출석 -> 결석]")
    void attendanceToAbsence() {
        // given
        final StudyAttendance attendance = StudyAttendance.recordAttendance(study.getId(), member.getId(), 1, ATTENDANCE);
        final ManualAttendanceCommand command = new ManualAttendanceCommand(study.getId(), member.getId(), 1, ABSENCE);
        given(studyAttendanceRepository.getParticipantAttendanceByWeek(command.studyId(), command.participantId(), command.week()))
                .willReturn(Optional.of(attendance));

        // when
        sut.invoke(command);

        // then
        assertAll(
                () -> verify(studyAttendanceRepository, times(1))
                        .getParticipantAttendanceByWeek(command.studyId(), command.participantId(), command.week()),
                () -> verify(memberRepository, times(1)).getById(command.participantId()),
                () -> assertThat(attendance.getStatus()).isEqualTo(ABSENCE),
                () -> assertThat(member.getScore().getValue()).isEqualTo(previousScore - Score.ATTENDANCE + Score.ABSENCE)
        );
    }

    @Test
    @DisplayName("수동 출석 체크를 진행한다 [지각 -> 출석]")
    void lateToAttendance() {
        // given
        final StudyAttendance attendance = StudyAttendance.recordAttendance(study.getId(), member.getId(), 1, LATE);
        final ManualAttendanceCommand command = new ManualAttendanceCommand(study.getId(), member.getId(), 1, ATTENDANCE);
        given(studyAttendanceRepository.getParticipantAttendanceByWeek(command.studyId(), command.participantId(), command.week()))
                .willReturn(Optional.of(attendance));

        // when
        sut.invoke(command);

        // then
        assertAll(
                () -> verify(studyAttendanceRepository, times(1))
                        .getParticipantAttendanceByWeek(command.studyId(), command.participantId(), command.week()),
                () -> verify(memberRepository, times(1)).getById(command.participantId()),
                () -> assertThat(attendance.getStatus()).isEqualTo(ATTENDANCE),
                () -> assertThat(member.getScore().getValue()).isEqualTo(previousScore - Score.LATE + Score.ATTENDANCE)
        );
    }

    @Test
    @DisplayName("수동 출석 체크를 진행한다 [지각 -> 결석]")
    void lateToAbsence() {
        // given
        final StudyAttendance attendance = StudyAttendance.recordAttendance(study.getId(), member.getId(), 1, LATE);
        final ManualAttendanceCommand command = new ManualAttendanceCommand(study.getId(), member.getId(), 1, ABSENCE);
        given(studyAttendanceRepository.getParticipantAttendanceByWeek(command.studyId(), command.participantId(), command.week()))
                .willReturn(Optional.of(attendance));

        // when
        sut.invoke(command);

        // then
        assertAll(
                () -> verify(studyAttendanceRepository, times(1))
                        .getParticipantAttendanceByWeek(command.studyId(), command.participantId(), command.week()),
                () -> verify(memberRepository, times(1)).getById(command.participantId()),
                () -> assertThat(attendance.getStatus()).isEqualTo(ABSENCE),
                () -> assertThat(member.getScore().getValue()).isEqualTo(previousScore - Score.LATE + Score.ABSENCE)
        );
    }

    @Test
    @DisplayName("수동 출석 체크를 진행한다 [결석 -> 출석]")
    void absenceToAttendance() {
        // given
        final StudyAttendance attendance = StudyAttendance.recordAttendance(study.getId(), member.getId(), 1, ABSENCE);
        final ManualAttendanceCommand command = new ManualAttendanceCommand(study.getId(), member.getId(), 1, ATTENDANCE);
        given(studyAttendanceRepository.getParticipantAttendanceByWeek(command.studyId(), command.participantId(), command.week()))
                .willReturn(Optional.of(attendance));

        // when
        sut.invoke(command);

        // then
        assertAll(
                () -> verify(studyAttendanceRepository, times(1))
                        .getParticipantAttendanceByWeek(command.studyId(), command.participantId(), command.week()),
                () -> verify(memberRepository, times(1)).getById(command.participantId()),
                () -> assertThat(attendance.getStatus()).isEqualTo(ATTENDANCE),
                () -> assertThat(member.getScore().getValue()).isEqualTo(previousScore - Score.ABSENCE + Score.ATTENDANCE)
        );
    }

    @Test
    @DisplayName("수동 출석 체크를 진행한다 [결석 -> 지각]")
    void absenceToLate() {
        // given
        final StudyAttendance attendance = StudyAttendance.recordAttendance(study.getId(), member.getId(), 1, ABSENCE);
        final ManualAttendanceCommand command = new ManualAttendanceCommand(study.getId(), member.getId(), 1, LATE);
        given(studyAttendanceRepository.getParticipantAttendanceByWeek(command.studyId(), command.participantId(), command.week()))
                .willReturn(Optional.of(attendance));

        // when
        sut.invoke(command);

        // then
        assertAll(
                () -> verify(studyAttendanceRepository, times(1))
                        .getParticipantAttendanceByWeek(command.studyId(), command.participantId(), command.week()),
                () -> verify(memberRepository, times(1)).getById(command.participantId()),
                () -> assertThat(attendance.getStatus()).isEqualTo(LATE),
                () -> assertThat(member.getScore().getValue()).isEqualTo(previousScore - Score.ABSENCE + Score.LATE)
        );
    }
}
