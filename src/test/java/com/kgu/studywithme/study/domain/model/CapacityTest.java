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
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Study -> 도메인 [Capacity VO] 테스트")
class CapacityTest extends ParallelTest {
    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 1, 11})
    @DisplayName("Capacity가 2..10 범위가 아니라면 생성할 수 없다")
    void throwExceptionByCapacityOutOfRange(final int value) {
        assertThatThrownBy(() -> new Capacity(value))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.CAPACITY_IS_OUT_OF_RANGE.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 5, 10})
    @DisplayName("Capacity를 생성한다")
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

    @Test
    @DisplayName("currentParticipant에 대해서 Capacity가 꽉 찼는지 확인한다")
    void isFull() {
        // given
        final Capacity capacity = new Capacity(5);

        // when
        final boolean actual1 = capacity.isFull(4);
        final boolean actual2 = capacity.isFull(5);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}
