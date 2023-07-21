package com.kgu.studywithme.study.domain;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Study -> 도메인 [Capacity VO] 테스트")
class CapacityTest {
    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 1, 11})
    @DisplayName("범위를 넘어가는 값에 의해서 Capacity 생성에 실패한다")
    void throwExceptionByCapacityOutOfRange(final int value) {
        assertThatThrownBy(() -> Capacity.from(value))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.CAPACITY_IS_OUT_OF_RANGE.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 5, 10})
    @DisplayName("수용할 수 있는 값으로 Capacity를 생성한다")
    void construct(final int value) {
        assertDoesNotThrow(() -> Capacity.from(value));
    }

    @ParameterizedTest
    @MethodSource("isEqualOrOver")
    @DisplayName("생성한 Capacity에 대해서 비교값이 같거나 큰지 판별한다")
    void isFullByCompareWith(final int compareValue, final boolean expected) {
        final Capacity capacity = Capacity.from(2);
        assertThat(capacity.isFullByCompareWith(compareValue)).isEqualTo(expected);
    }

    private static Stream<Arguments> isEqualOrOver() {
        return Stream.of(
                Arguments.of(3, true),
                Arguments.of(2, true),
                Arguments.of(1, false)
        );
    }
}
