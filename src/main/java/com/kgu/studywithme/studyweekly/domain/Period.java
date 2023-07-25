package com.kgu.studywithme.studyweekly.domain;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.studyweekly.exception.StudyWeeklyErrorCode;
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

    public Period(
            final LocalDateTime startDate,
            final LocalDateTime endDate
    ) {
        validateStartIsBeforeEnd(startDate, endDate);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    private void validateStartIsBeforeEnd(
            final LocalDateTime startDate,
            final LocalDateTime endDate
    ) {
        if (startDate.isAfter(endDate)) {
            throw StudyWithMeException.type(StudyWeeklyErrorCode.PERIOD_START_DATE_MUST_BE_BEFORE_END_DATE);
        }
    }

    public boolean isDateWithInRange(final LocalDateTime time) {
        return startDate.isBefore(time) && endDate.isAfter(time);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Period other = (Period) o;

        if (!startDate.equals(other.startDate)) return false;
        return endDate.equals(other.endDate);
    }

    @Override
    public int hashCode() {
        int result = startDate.hashCode();
        result = 31 * result + endDate.hashCode();
        return result;
    }
}
