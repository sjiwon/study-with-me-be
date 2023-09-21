package com.kgu.studywithme.study.domain.model;

import com.kgu.studywithme.common.ParallelTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Study -> 도메인 [StudyName VO] 테스트")
class StudyNameTest extends ParallelTest {
    @Test
    @DisplayName("StudyName이 공백이면 생성에 실패한다")
    void throwExceptionByNameIsBlank() {
        assertThatThrownBy(() -> new StudyName(""))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.NAME_IS_BLANK.getMessage());
    }

    @Test
    @DisplayName("StudyName이 길이 제한을 넘어선다면 생성에 실패한다")
    void throwExceptionByNameLengthIsOutOfRange() {
        assertThatThrownBy(() -> new StudyName("a".repeat(21)))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.NAME_LENGTH_IS_OUT_OF_RANGE.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "aaaaaaaaaaaaaaaaaaaa"})
    @DisplayName("StudyName을 생성한다")
    void construct(final String value) {
        assertDoesNotThrow(() -> new StudyName(value));
    }
}
