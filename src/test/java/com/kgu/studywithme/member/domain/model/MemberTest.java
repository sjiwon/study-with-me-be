package com.kgu.studywithme.member.domain.model;

import com.kgu.studywithme.common.ParallelTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.common.fixture.MemberFixture.ANONYMOUS;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.ABSENCE;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.ATTENDANCE;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.LATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member -> 도메인 [Member] 테스트")
class MemberTest extends ParallelTest {
    @Test
    @DisplayName("Member를 생성한다")
    void constuct() {
        final Member member = JIWON.toMember();

        assertAll(
                () -> assertThat(member.getName()).isEqualTo(JIWON.getName()),
                () -> assertThat(member.getNickname()).isEqualTo(JIWON.getNickname()),
                () -> assertThat(member.getEmail()).isEqualTo(JIWON.getEmail()),
                () -> assertThat(member.isEmailOptIn()).isEqualTo(JIWON.getEmail().isEmailOptIn()),
                () -> assertThat(member.getBirth()).isEqualTo(JIWON.getBirth()),
                () -> assertThat(member.getPhone()).isEqualTo(JIWON.getPhone()),
                () -> assertThat(member.getGender()).isEqualTo(JIWON.getGender()),
                () -> assertThat(member.getAddress()).isEqualTo(JIWON.getAddress()),
                () -> assertThat(member.getScore().getValue()).isEqualTo(Score.INIT_SCORE),
                () -> assertThat(member.getInterests()).containsExactlyInAnyOrderElementsOf(JIWON.getInterests())
        );
    }

    @Test
    @DisplayName("사용자 정보를 수정한다")
    void update() {
        // given
        final Member member = JIWON.toMember().apply(1L);

        // when
        member.update(
                ANONYMOUS.getNickname(),
                ANONYMOUS.getPhone(),
                ANONYMOUS.getAddress(),
                ANONYMOUS.getEmail().isEmailOptIn(),
                ANONYMOUS.getInterests()
        );

        // then
        assertAll(
                () -> assertThat(member.getName()).isEqualTo(JIWON.getName()),
                () -> assertThat(member.getNickname()).isEqualTo(ANONYMOUS.getNickname()),
                () -> assertThat(member.getEmail()).isEqualTo(JIWON.getEmail()),
                () -> assertThat(member.isEmailOptIn()).isEqualTo(ANONYMOUS.getEmail().isEmailOptIn()),
                () -> assertThat(member.getBirth()).isEqualTo(JIWON.getBirth()),
                () -> assertThat(member.getPhone()).isEqualTo(ANONYMOUS.getPhone()),
                () -> assertThat(member.getGender()).isEqualTo(JIWON.getGender()),
                () -> assertThat(member.getAddress()).isEqualTo(ANONYMOUS.getAddress()),
                () -> assertThat(member.getScore().getValue()).isEqualTo(Score.INIT_SCORE),
                () -> assertThat(member.getInterests()).containsExactlyInAnyOrderElementsOf(ANONYMOUS.getInterests())
        );
    }

    @Nested
    @DisplayName("사용자 점수 업데이트")
    class UpdateScore {
        private Member member;
        private int previousScore;

        @BeforeEach
        void setUp() {
            member = JIWON.toMember();
            previousScore = member.getScore().getValue();
        }

        @Nested
        @DisplayName("단순 출석에 대한 점수 업데이트")
        class ApplySimpleAttendance {
            @Test
            @DisplayName("출석에 대한 점수를 적용한다")
            void applyAttendance() {
                // when
                member.applyScoreByAttendanceStatus(ATTENDANCE);

                // then
                assertThat(member.getScore().getValue()).isEqualTo(previousScore + Score.ATTENDANCE);
            }

            @Test
            @DisplayName("지각에 대한 점수를 적용한다")
            void applyLate() {
                // when
                member.applyScoreByAttendanceStatus(LATE);

                // then
                assertThat(member.getScore().getValue()).isEqualTo(previousScore + Score.LATE);
            }

            @Test
            @DisplayName("결석에 대한 점수를 적용한다")
            void applyAbsence() {
                // when
                member.applyScoreByAttendanceStatus(ABSENCE);

                // then
                assertThat(member.getScore().getValue()).isEqualTo(previousScore + Score.ABSENCE);
            }
        }

        @Nested
        @DisplayName("이전 출석 정보 수정에 따른 점수 업데이트")
        class ApplyComplexAttendance {
            @Test
            @DisplayName("출석 -> 지각으로 수정함에 따라 점수를 업데이트한다")
            void updateAttendanceToLate() {
                // when
                member.applyScoreByAttendanceStatus(ATTENDANCE, LATE);

                // then
                assertThat(member.getScore().getValue()).isEqualTo(previousScore - Score.ATTENDANCE + Score.LATE);
            }

            @Test
            @DisplayName("출석 -> 결석으로 수정함에 따라 점수를 업데이트한다")
            void updateAttendanceToAbsence() {
                // when
                member.applyScoreByAttendanceStatus(ATTENDANCE, ABSENCE);

                // then
                assertThat(member.getScore().getValue()).isEqualTo(previousScore - Score.ATTENDANCE + Score.ABSENCE);
            }

            @Test
            @DisplayName("지각 -> 출석으로 수정함에 따라 점수를 업데이트한다")
            void updateLateToAttendance() {
                // when
                member.applyScoreByAttendanceStatus(LATE, ATTENDANCE);

                // then
                assertThat(member.getScore().getValue()).isEqualTo(previousScore - Score.LATE + Score.ATTENDANCE);
            }

            @Test
            @DisplayName("지각 -> 결석으로 수정함에 따라 점수를 업데이트한다")
            void updateLateToAbsence() {
                // when
                member.applyScoreByAttendanceStatus(LATE, ABSENCE);

                // then
                assertThat(member.getScore().getValue()).isEqualTo(previousScore - Score.LATE + Score.ABSENCE);
            }

            @Test
            @DisplayName("결석 -> 출석으로 수정함에 따라 점수를 업데이트한다")
            void updateAbsenceToAttendance() {
                // when
                member.applyScoreByAttendanceStatus(ABSENCE, ATTENDANCE);

                // then
                assertThat(member.getScore().getValue()).isEqualTo(previousScore - Score.ABSENCE + Score.ATTENDANCE);
            }

            @Test
            @DisplayName("결석 -> 지각으로 수정함에 따라 점수를 업데이트한다")
            void updateAbsenceToLate() {
                // when
                member.applyScoreByAttendanceStatus(ABSENCE, LATE);

                // then
                assertThat(member.getScore().getValue()).isEqualTo(previousScore - Score.ABSENCE + Score.LATE);
            }
        }
    }
}
