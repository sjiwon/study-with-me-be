package com.kgu.studywithme.study.domain.week;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Period {
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    private Period(
            final LocalDateTime startDate,
            final LocalDateTime endDate
    ) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static Period of(
            final LocalDateTime startDate,
            final LocalDateTime endDate
    ) {
        validateStartIsBeforeEnd(startDate, endDate);
        return new Period(startDate, endDate);
    }

    private static void validateStartIsBeforeEnd(
            final LocalDateTime startDate,
            final LocalDateTime endDate
    ) {
        if (startDate.isAfter(endDate)) {
            throw StudyWithMeException.type(StudyErrorCode.PERIOD_START_DATE_MUST_BE_BEFORE_END_DATE);
        }
    }

    public boolean isDateWithInRange(final LocalDateTime time) {
        return startDate.isBefore(time) && endDate.isAfter(time);
    }
}
