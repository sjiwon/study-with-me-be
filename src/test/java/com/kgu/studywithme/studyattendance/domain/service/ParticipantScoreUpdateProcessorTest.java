package com.kgu.studywithme.studyattendance.domain.service;

import com.kgu.studywithme.common.ParallelTest;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.model.Score;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.ABSENCE;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.ATTENDANCE;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.LATE;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.NON_ATTENDANCE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@DisplayName("StudyAttendance -> ParticipantScoreUpdateProcessor 테스트")
public class ParticipantScoreUpdateProcessorTest extends ParallelTest {
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final ParticipantScoreUpdateProcessor sut = new ParticipantScoreUpdateProcessor(memberRepository);

    private Member member;
    private int previousScore;

    @BeforeEach
    void setUp() {
        member = JIWON.toMember().apply(1L);
        previousScore = member.getScore().getValue();
        given(memberRepository.getById(member.getId())).willReturn(member);
    }

    @Nested
    @DisplayName("스터디 참여자 Score 업데이트 (이전 상태 = NON_ATTENDANCE")
    class PrevioudStatusIsNonAttendance {
        @Test
        @DisplayName("미출결 -> 출석 수정에 대한 Score 업데이트")
        void nonAttendanceToAttendance() {
            // when
            sut.updateByAttendanceStatus(member.getId(), NON_ATTENDANCE, ATTENDANCE);

            // then
            assertThat(member.getScore().getValue()).isEqualTo(previousScore + Score.ATTENDANCE);
        }

        @Test
        @DisplayName("미출결 -> 지각 수정에 대한 Score 업데이트")
        void nonAttendanceToLate() {
            // when
            sut.updateByAttendanceStatus(member.getId(), NON_ATTENDANCE, LATE);

            // then
            assertThat(member.getScore().getValue()).isEqualTo(previousScore + Score.LATE);
        }

        @Test
        @DisplayName("미출결 -> 결석 수정에 대한 Score 업데이트")
        void nonAttendanceToAbsence() {
            // when
            sut.updateByAttendanceStatus(member.getId(), NON_ATTENDANCE, ABSENCE);

            // then
            assertThat(member.getScore().getValue()).isEqualTo(previousScore + Score.ABSENCE);
        }
    }

    @Nested
    @DisplayName("스터디 참여자 Score 업데이트 (이전 상태 != NON_ATTENDANCE")
    class PrevioudStatusIsNotNonAttendance {
        @Test
        @DisplayName("출석 -> 지각 수정에 대한 Score 업데이트")
        void attendanceToLate() {
            // when
            sut.updateByAttendanceStatus(member.getId(), ATTENDANCE, LATE);

            // then
            assertThat(member.getScore().getValue()).isEqualTo(previousScore - Score.ATTENDANCE + Score.LATE);
        }

        @Test
        @DisplayName("출석 -> 결석 수정에 대한 Score 업데이트")
        void attendanceToAbsence() {
            // when
            sut.updateByAttendanceStatus(member.getId(), ATTENDANCE, ABSENCE);

            // then
            assertThat(member.getScore().getValue()).isEqualTo(previousScore - Score.ATTENDANCE + Score.ABSENCE);
        }

        @Test
        @DisplayName("지각 -> 출석 수정에 대한 Score 업데이트")
        void lateToAttendance() {
            // when
            sut.updateByAttendanceStatus(member.getId(), LATE, ATTENDANCE);

            // then
            assertThat(member.getScore().getValue()).isEqualTo(previousScore - Score.LATE + Score.ATTENDANCE);
        }

        @Test
        @DisplayName("지각 -> 결석 수정에 대한 Score 업데이트")
        void lateToAbsence() {
            // when
            sut.updateByAttendanceStatus(member.getId(), LATE, ABSENCE);

            // then
            assertThat(member.getScore().getValue()).isEqualTo(previousScore - Score.LATE + Score.ABSENCE);
        }

        @Test
        @DisplayName("결석 -> 출석 수정에 대한 Score 업데이트")
        void absenceToAttendance() {
            // when
            sut.updateByAttendanceStatus(member.getId(), ABSENCE, ATTENDANCE);

            // then
            assertThat(member.getScore().getValue()).isEqualTo(previousScore - Score.ABSENCE + Score.ATTENDANCE);
        }

        @Test
        @DisplayName("결석 -> 지각 수정에 대한 Score 업데이트")
        void absenceToLate() {
            // when
            sut.updateByAttendanceStatus(member.getId(), ABSENCE, LATE);

            // then
            assertThat(member.getScore().getValue()).isEqualTo(previousScore - Score.ABSENCE + Score.LATE);
        }
    }
}
