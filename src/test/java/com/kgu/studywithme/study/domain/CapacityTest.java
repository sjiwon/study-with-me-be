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

    @ParameterizedTest
    @MethodSource("isEqualOrLessThan")
    @DisplayName("비교값에 대해서 Capacity가 같거나 더 작음을 판별한다")
    void isEqualOrLessThan(final int compareValue, final boolean expected) {
        final Capacity capacity = new Capacity(2);
        assertThat(capacity.isEqualOrLessThan(compareValue)).isEqualTo(expected);
    }

    private static Stream<Arguments> isEqualOrLessThan() {
        return Stream.of(
                Arguments.of(3, true),
                Arguments.of(2, true),
                Arguments.of(1, false)
        );
    }

    @ParameterizedTest
    @MethodSource("isLessThan")
    @DisplayName("비교값에 대해서 Capacity가 더 작음을 판별한다")
    void isLessThan(final int compareValue, final boolean expected) {
        final Capacity capacity = new Capacity(2);
        assertThat(capacity.isLessThan(compareValue)).isEqualTo(expected);
    }

    private static Stream<Arguments> isLessThan() {
        return Stream.of(
                Arguments.of(3, true),
                Arguments.of(2, false),
                Arguments.of(1, false)
        );
    }
}
