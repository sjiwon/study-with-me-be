package com.kgu.studywithme.member.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Member -> 도메인 [Score VO] 테스트")
class ScoreTest {
    @Test
    @DisplayName("Score를 생성한다")
    void construct() {
        final Score score = Score.initScore();

        assertThat(score.getValue()).isEqualTo(80);
    }

    @Test
    @DisplayName("Maximum을 넘어선 Score를 설정할 경우 Maximum 값으로 자동 조정된다")
    void maximum() {
        // when
        final Score score = new Score(10000);

        // then
        assertThat(score.getValue()).isEqualTo(100);
    }

    @Test
    @DisplayName("Minimum보다 낮은 Score를 설정할 경우 Minimum 값으로 자동 조정된다")
    void minimum() {
        // when
        final Score score = new Score(-10000);

        // then
        assertThat(score.getValue()).isEqualTo(0);
    }

    @Nested
    @DisplayName("단순 출석에 대한 Score 업데이트")
    class ApplySimpleAttendance {
        private Score score;

        @BeforeEach
        void setUp() {
            score = Score.initScore(); // 80
        }

        @Test
        @DisplayName("출석에 대한 Score를 업데이트한다")
        void attendance() {
            // when
            final Score updateScore = score.applyAttendance(); // 80 + 1

            // then
            assertThat(updateScore.getValue()).isEqualTo(81);
        }

        @Test
        @DisplayName("지각에 대한 Score를 업데이트한다")
        void late() {
            // when
            final Score updateScore = score.applyLate(); // 80 - 1

            // then
            assertThat(updateScore.getValue()).isEqualTo(79);
        }

        @Test
        @DisplayName("결석에 대한 Score를 업데이트한다")
        void absence() {
            // when
            final Score updateScore = score.applyAbsence(); // 80 - 5

            // then
            assertThat(updateScore.getValue()).isEqualTo(75);
        }
    }

    @Nested
    @DisplayName("이전 출석 정보 수정에 따른 Score 업데이트")
    class ApplyComplexAttendance {
        private Score score;

        @BeforeEach
        void setUp() {
            score = Score.initScore(); // 80
        }

        @Test
        @DisplayName("출석 -> 지각으로 수정함에 따라 Score를 업데이트한다")
        void updateAttendanceToLate() {
            // when
            final Score updateScore = score.updateAttendanceToLate(); // 80 - 1 - 1

            // then
            assertThat(updateScore.getValue()).isEqualTo(78);
        }

        @Test
        @DisplayName("출석 -> 결석으로 수정함에 따라 Score를 업데이트한다")
        void updateAttendanceToAbsence() {
            // when
            final Score updateScore = score.updateAttendanceToAbsence(); // 80 - 1 - 5

            // then
            assertThat(updateScore.getValue()).isEqualTo(74);
        }

        @Test
        @DisplayName("지각 -> 출석으로 수정함에 따라 Score를 업데이트한다")
        void updateLateToAttendance() {
            // when
            final Score updateScore = score.updateLateToAttendance(); // 80 + 1 + 1

            // then
            assertThat(updateScore.getValue()).isEqualTo(82);
        }

        @Test
        @DisplayName("지각 -> 결석으로 수정함에 따라 Score를 업데이트한다")
        void updateLateToAbsence() {
            // when
            final Score updateScore = score.updateLateToAbsence(); // 80 + 1 - 5

            // then
            assertThat(updateScore.getValue()).isEqualTo(76);
        }

        @Test
        @DisplayName("결석 -> 출석으로 수정함에 따라 Score를 업데이트한다")
        void updateAbsenceToAttendance() {
            // when
            final Score updateScore = score.updateAbsenceToAttendance(); // 80 + 5 + 1

            // then
            assertThat(updateScore.getValue()).isEqualTo(86);
        }

        @Test
        @DisplayName("결석 -> 지각으로 수정함에 따라 Score를 업데이트한다")
        void updateAbsenceToLate() {
            // when
            final Score updateScore = score.updateAbsenceToLate(); // 80 + 5 - 1

            // then
            assertThat(updateScore.getValue()).isEqualTo(84);
        }
    }
}
