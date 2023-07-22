package com.kgu.studywithme.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Score {
    // 초기 세팅값
    private static final int DEFAULT_INIT_VALUE = 80;
    private static final int MINIMUM = 0;
    private static final int MAXIMUM = 100;

    // 출석 관련 점수
    private static final int ATTENDANCE = 1;
    private static final int LATE = 1;
    private static final int ABSENCE = 5;

    @Column(name = "score", nullable = false)
    private int value;

    private Score(final int value) {
        this.value = value < MINIMUM ? MINIMUM : Math.min(value, MAXIMUM);
    }

    public static Score from(final int value) {
        return new Score(value);
    }

    public static Score initScore() {
        return new Score(DEFAULT_INIT_VALUE);
    }

    public Score applyAttendance() {
        return new Score(value + ATTENDANCE);
    }

    public Score applyLate() {
        return new Score(value - LATE);
    }

    public Score applyAbsence() {
        return new Score(value - ABSENCE);
    }

    public Score updateAttendanceToLate() {
        return new Score(value - ATTENDANCE - LATE);
    }

    public Score updateAttendanceToAbsence() {
        return new Score(value - ATTENDANCE - ABSENCE);
    }

    public Score updateLateToAttendance() {
        return new Score(value + LATE + ATTENDANCE);
    }

    public Score updateLateToAbsence() {
        return new Score(value + LATE - ABSENCE);
    }

    public Score updateAbsenceToAttendance() {
        return new Score(value + ABSENCE + ATTENDANCE);
    }

    public Score updateAbsenceToLate() {
        return new Score(value + ABSENCE - LATE);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Score other = (Score) o;

        return value == other.value;
    }

    @Override
    public int hashCode() {
        return value;
    }
}
