package com.kgu.studywithme.studyweekly.domain.model;

import com.kgu.studywithme.common.ExecuteParallel;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studyweekly.exception.StudyWeeklyErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExecuteParallel
@DisplayName("StudyWeekly -> 도메인 [Period VO] 테스트")
class PeriodTest {
    @Test
    @DisplayName("시작일이 종료일보다 늦는다면 Period 생성에 실패한다")
    void throwExceptionByPeriodStartDateMustBeBeforeEndDate() {
        final LocalDateTime startDate = LocalDateTime.now().plusDays(7);
        final LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        assertThatThrownBy(() -> new Period(startDate, endDate))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyWeeklyErrorCode.PERIOD_START_DATE_MUST_BE_BEFORE_END_DATE.getMessage());
    }

    @Test
    @DisplayName("Period을 생성한다")
    void construct() {
        assertDoesNotThrow(() -> new Period(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));
    }

    @Test
    @DisplayName("주어진 날짜가 Period의 StartDate ~ EndDate 사이에 포함되는지 확인한다")
    void isDateWithInRange() {
        // given
        final Period period = new Period(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(7));

        // when
        final boolean actual1 = period.isDateWithInRange(LocalDateTime.now().plusDays(4));
        final boolean actual2 = period.isDateWithInRange(LocalDateTime.now().plusDays(8));

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}
