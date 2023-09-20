package com.kgu.studywithme.study.domain.model;

import com.kgu.studywithme.common.ParallelTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study -> 도메인 [GraduationPolicy VO] 테스트")
class GraduationPolicyTest extends ParallelTest {
    @Test
    @DisplayName("GraduationPolicy를 생성한다")
    void construct() {
        final GraduationPolicy policy = GraduationPolicy.initPolicy(10);

        assertAll(
                () -> assertThat(policy.getMinimumAttendance()).isEqualTo(10),
                () -> assertThat(policy.getUpdateChance()).isEqualTo(GraduationPolicy.DEFAULT_UPDATE_CHANCE)
        );
    }

    @Nested
    @DisplayName("GraduationPoicy 수정")
    class Update {
        private GraduationPolicy policy;
        private int previousMinimumAttendance;
        private int previousUpdateChance;

        @BeforeEach
        void setUp() {
            policy = GraduationPolicy.initPolicy(10);
            previousMinimumAttendance = policy.getMinimumAttendance();
            previousUpdateChance = policy.getUpdateChance();
        }

        @Test
        @DisplayName("수정할 기회가 남아있지 않음에 따라 GraduationPolicy를 수정할 수 없다")
        void throwExceptionByNoChanceToUpdateGraduationPolicy() {
            // given
            ReflectionTestUtils.setField(policy, "updateChance", 0);

            // when - then
            assertThatThrownBy(() -> policy.update(previousMinimumAttendance + 10))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.NO_CHANCE_TO_UPDATE_GRADUATION_POLICY.getMessage());
        }

        @Test
        @DisplayName("GraduationPolicy의 MinimumAttendance를 수정한다 [변화 X]")
        void success1() {
            // when
            final GraduationPolicy update = policy.update(previousMinimumAttendance);

            // then
            assertAll(
                    () -> assertThat(update.getMinimumAttendance()).isEqualTo(previousMinimumAttendance),
                    () -> assertThat(update.getUpdateChance()).isEqualTo(previousUpdateChance)
            );
        }

        @Test
        @DisplayName("GraduationPolicy의 MinimumAttendance를 수정한다 [변화 O]")
        void success2() {
            // when
            final GraduationPolicy update = policy.update(previousMinimumAttendance + 10);

            // then
            assertAll(
                    () -> assertThat(update.getMinimumAttendance()).isEqualTo(previousMinimumAttendance + 10),
                    () -> assertThat(update.getUpdateChance()).isEqualTo(previousUpdateChance - 1)
            );
        }
    }

    @Test
    @DisplayName("GraduationPolicy의 UpdateChance를 초기화한다")
    void resetUpdateChance() {
        // given
        final GraduationPolicy policy = GraduationPolicy
                .initPolicy(10) // remain = 3
                .update(15) // remain = 2
                .update(13); // remain = 1

        // when
        final GraduationPolicy resetUpdateChange = policy.resetUpdateChance();

        // then
        assertAll(
                () -> assertThat(resetUpdateChange.getMinimumAttendance()).isEqualTo(13),
                () -> assertThat(resetUpdateChange.getUpdateChance()).isEqualTo(GraduationPolicy.DEFAULT_UPDATE_CHANCE)
        );
    }

    @Test
    @DisplayName("졸업 요건을 충족했는지 여부를 확인한다")
    void isGraduationRequirementsFulfilled() {
        // given
        final GraduationPolicy policy = GraduationPolicy.initPolicy(10);

        // when
        final boolean actual1 = policy.isGraduationRequirementsFulfilled(9);
        final boolean actual2 = policy.isGraduationRequirementsFulfilled(10);
        final boolean actual3 = policy.isGraduationRequirementsFulfilled(11);

        // then
        assertAll(
                () -> assertThat(actual1).isFalse(),
                () -> assertThat(actual2).isTrue(),
                () -> assertThat(actual3).isTrue()
        );
    }
}
