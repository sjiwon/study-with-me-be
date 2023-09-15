package com.kgu.studywithme.study.domain.model;

import com.kgu.studywithme.common.ParallelTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Study -> 도메인 [Capacity VO] 테스트")
class CapacityTest extends ParallelTest {
    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 1, 11})
    @DisplayName("범위를 넘어가는 값에 의해서 Capacity 생성에 실패한다")
    void throwExceptionByCapacityOutOfRange(final int value) {
        assertThatThrownBy(() -> new Capacity(value))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.CAPACITY_IS_OUT_OF_RANGE.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 5, 10})
    @DisplayName("수용할 수 있는 값으로 Capacity를 생성한다")
    void construct(final int value) {
        assertDoesNotThrow(() -> new Capacity(value));
    }

    @Nested
    @DisplayName("Capacity 업데이트")
    class Update {
        @Test
        @DisplayName("수정하려는 Capacity는 현재 스터디 참여자 수보다 높아야 한다")
        void throwExceptionByCapacityCannotCoverCurrentParticipants() {
            // given
            final Capacity capacity = new Capacity(5);
            final int newCapacity = 2;
            final int currentParticipants = 3;

            // when - then
            assertThatThrownBy(() -> capacity.update(newCapacity, currentParticipants))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(StudyErrorCode.CAPACITY_CANNOT_COVER_CURRENT_PARTICIPANTS.getMessage());
        }

        @Test
        @DisplayName("Capacity를 업데이트한다")
        void success() {
            // given
            final Capacity capacity = new Capacity(5);
            final int newCapacity = 3;
            final int currentParticipants = 3;

            // when
            final Capacity updateCapacity = capacity.update(newCapacity, currentParticipants);

            // then
            assertThat(updateCapacity.getValue()).isEqualTo(3);
        }
    }
}
