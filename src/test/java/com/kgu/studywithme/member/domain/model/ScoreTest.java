package com.kgu.studywithme.member.domain.model;

import com.kgu.studywithme.common.ParallelTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Member -> 도메인 [Score VO] 테스트")
class ScoreTest extends ParallelTest {
    @Test
    @DisplayName("Score를 생성한다")
    void construct() {
        final Score score = Score.init();

        assertThat(score.getValue()).isEqualTo(Score.INIT_SCORE);
    }

    @Test
    @DisplayName("Maximum을 넘어선 Score를 설정할 경우 Maximum 값으로 자동 조정된다")
    void maximum() {
        // when
        final Score score = new Score(10000);

        // then
        assertThat(score.getValue()).isEqualTo(Score.MAXIMUM);
    }

    @Test
    @DisplayName("Minimum보다 낮은 Score를 설정할 경우 Minimum 값으로 자동 조정된다")
    void minimum() {
        // when
        final Score score = new Score(-10000);

        // then
        assertThat(score.getValue()).isEqualTo(Score.MINIMUM);
    }

    @Nested
    @DisplayName("단순 출석에 대한 Score 업데이트")
    class ApplySimpleAttendance {
        private Score score;
        private int previousScore;

        @BeforeEach
        void setUp() {
            score = Score.init();
            previousScore = score.getValue();
        }

        @Test
        @DisplayName("출석에 대한 Score를 업데이트한다")
        void attendance() {
            // when
            final Score updateScore = score.applyAttendance();

            // then
            assertThat(updateScore.getValue()).isEqualTo(previousScore + Score.ATTENDANCE);
        }

        @Test
        @DisplayName("지각에 대한 Score를 업데이트한다")
        void late() {
            // when
            final Score updateScore = score.applyLate();

            // then
            assertThat(updateScore.getValue()).isEqualTo(previousScore + Score.LATE);
        }

        @Test
        @DisplayName("결석에 대한 Score를 업데이트한다")
        void absence() {
            // when
            final Score updateScore = score.applyAbsence();

            // then
            assertThat(updateScore.getValue()).isEqualTo(previousScore + Score.ABSENCE);
        }
    }

    @Nested
    @DisplayName("이전 출석 정보 수정에 따른 Score 업데이트")
    class ApplyComplexAttendance {
        private Score score;
        private int previousScore;

        @BeforeEach
        void setUp() {
            score = Score.init();
            previousScore = score.getValue();
        }

        @Test
        @DisplayName("출석 -> 지각으로 수정함에 따라 Score를 업데이트한다")
        void updateAttendanceToLate() {
            // when
            final Score updateScore = score.updateAttendanceToLate();

            // then
            assertThat(updateScore.getValue()).isEqualTo(previousScore - Score.ATTENDANCE + Score.LATE);
        }

        @Test
        @DisplayName("출석 -> 결석으로 수정함에 따라 Score를 업데이트한다")
        void updateAttendanceToAbsence() {
            // when
            final Score updateScore = score.updateAttendanceToAbsence();

            // then
            assertThat(updateScore.getValue()).isEqualTo(previousScore - Score.ATTENDANCE + Score.ABSENCE);
        }

        @Test
        @DisplayName("지각 -> 출석으로 수정함에 따라 Score를 업데이트한다")
        void updateLateToAttendance() {
            // when
            final Score updateScore = score.updateLateToAttendance();

            // then
            assertThat(updateScore.getValue()).isEqualTo(previousScore - Score.LATE + Score.ATTENDANCE);
        }

        @Test
        @DisplayName("지각 -> 결석으로 수정함에 따라 Score를 업데이트한다")
        void updateLateToAbsence() {
            // when
            final Score updateScore = score.updateLateToAbsence();

            // then
            assertThat(updateScore.getValue()).isEqualTo(previousScore - Score.LATE + Score.ABSENCE);
        }

        @Test
        @DisplayName("결석 -> 출석으로 수정함에 따라 Score를 업데이트한다")
        void updateAbsenceToAttendance() {
            // when
            final Score updateScore = score.updateAbsenceToAttendance();

            // then
            assertThat(updateScore.getValue()).isEqualTo(previousScore - Score.ABSENCE + Score.ATTENDANCE);
        }

        @Test
        @DisplayName("결석 -> 지각으로 수정함에 따라 Score를 업데이트한다")
        void updateAbsenceToLate() {
            // when
            final Score updateScore = score.updateAbsenceToLate();

            // then
            assertThat(updateScore.getValue()).isEqualTo(previousScore - Score.ABSENCE + Score.LATE);
        }
    }
}
