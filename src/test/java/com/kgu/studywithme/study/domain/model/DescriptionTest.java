package com.kgu.studywithme.study.domain.model;

import com.kgu.studywithme.common.ParallelTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Study -> 도메인 [Description VO] 테스트")
class DescriptionTest extends ParallelTest {
    @Test
    @DisplayName("Description이 공백이면 생성에 실패한다")
    void throwExceptionByDescriptionIsBlank() {
        assertThatThrownBy(() -> new Description(""))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.DESCRIPTION_IS_BLANK.getMessage());
    }

    @Test
    @DisplayName("Description을 생성한다")
    void construct() {
        assertDoesNotThrow(() -> new Description("a".repeat(999)));
    }
}
