package com.kgu.studywithme.fixture;

import com.kgu.studywithme.studyweekly.domain.Period;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public enum PeriodFixture {
    WEEK_0(LocalDateTime.now().minusDays(11), LocalDateTime.now().minusDays(4)),
    WEEK_1(LocalDateTime.now().minusDays(3), LocalDateTime.now().plusDays(3)),
    WEEK_2(LocalDateTime.now().plusDays(4), LocalDateTime.now().plusDays(11)),
    WEEK_3(LocalDateTime.now().plusDays(12), LocalDateTime.now().plusDays(19)),
    WEEK_4(LocalDateTime.now().plusDays(20), LocalDateTime.now().plusDays(27)),
    WEEK_5(LocalDateTime.now().plusDays(28), LocalDateTime.now().plusDays(35)),
    WEEK_6(LocalDateTime.now().plusDays(36), LocalDateTime.now().plusDays(42)),
    ;

    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    public Period toPeriod() {
        return new Period(startDate, endDate);
    }
}
