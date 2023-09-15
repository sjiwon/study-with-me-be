package com.kgu.studywithme.studyattendance.application.service;

import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.studyattendance.application.usecase.command.ManualAttendanceUseCase;
import com.kgu.studywithme.studyattendance.domain.model.StudyAttendance;
import com.kgu.studywithme.studyattendance.domain.repository.StudyAttendanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.ABSENCE;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.LATE;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.NON_ATTENDANCE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("StudyAttendance -> ManualAttendanceService 테스트")
class ManualAttendanceServiceTest extends UseCaseTest {
    @InjectMocks
    private ManualAttendanceService manualAttendanceService;

    @Mock
    private StudyAttendanceRepository studyAttendanceRepository;

    @Mock
    private MemberRepository memberRepository;

    private Member member;
    private int previousScore;
    private Study study;

    @BeforeEach
    void setUp() {
        member = JIWON.toMember().apply(1L, LocalDateTime.now());
        previousScore = member.getScore().getValue();
        study = SPRING.toOnlineStudy(member.getId()).apply(1L, LocalDateTime.now());
    }

    @Test
    @DisplayName("수동 출석 체크를 진행한다 [이전 상태 = NON_ATTENDANCE]")
    void successCaseA() {
        // given
        final StudyAttendance attendance = StudyAttendance.recordAttendance(
                study.getId(),
                member.getId(),
                1,
                NON_ATTENDANCE
        );
        given(studyAttendanceRepository.getParticipantAttendanceByWeek(any(), any(), any())).willReturn(Optional.of(attendance));
        given(memberRepository.getById(any())).willReturn(member);

        // when
        manualAttendanceService.invoke(
                new ManualAttendanceUseCase.Command(
                        study.getId(),
                        member.getId(),
                        1,
                        LATE // 미출결 -> 지각 = previousScore - 1
                )
        );

        // then
        assertAll(
                () -> verify(studyAttendanceRepository, times(1)).getParticipantAttendanceByWeek(any(), any(), any()),
                () -> verify(memberRepository, times(1)).getById(any()),
                () -> assertThat(attendance.getStatus()).isEqualTo(LATE),
                () -> assertThat(member.getScore().getValue()).isEqualTo(previousScore - 1)
        );
    }

    @Test
    @DisplayName("수동 출석 체크를 진행한다 [이전 상태 != 변경 상태]")
    void successCaseB() {
        // given
        final StudyAttendance attendance = StudyAttendance.recordAttendance(
                study.getId(),
                member.getId(),
                1,
                ABSENCE
        );
        given(studyAttendanceRepository.getParticipantAttendanceByWeek(any(), any(), any())).willReturn(Optional.of(attendance));
        given(memberRepository.getById(any())).willReturn(member);

        // when
        manualAttendanceService.invoke(
                new ManualAttendanceUseCase.Command(
                        study.getId(),
                        member.getId(),
                        1,
                        LATE // 결석 -> 지각 :: previousScore + 4
                )
        );

        // then
        assertAll(
                () -> verify(studyAttendanceRepository, times(1)).getParticipantAttendanceByWeek(any(), any(), any()),
                () -> verify(memberRepository, times(1)).getById(any()),
                () -> assertThat(attendance.getStatus()).isEqualTo(LATE),
                () -> assertThat(member.getScore().getValue()).isEqualTo(previousScore + 4)
        );
    }
}
