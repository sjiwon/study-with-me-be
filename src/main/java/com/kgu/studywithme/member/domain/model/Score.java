package com.kgu.studywithme.member.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Score {
    public static final int INIT_SCORE = 80;
    public static final int MINIMUM = 0;
    public static final int MAXIMUM = 100;

    // 출석 관련 점수
    public static final int ATTENDANCE = 1;
    public static final int LATE = -1;
    public static final int ABSENCE = -5;

    @Column(name = "score", nullable = false)
    private int value;

    public Score(final int value) {
        this.value = value < MINIMUM ? MINIMUM : Math.min(value, MAXIMUM);
    }

    public static Score initScore() {
        return new Score(INIT_SCORE);
    }

    public Score applyAttendance() {
        return new Score(value + ATTENDANCE);
    }

    public Score applyLate() {
        return new Score(value + LATE);
    }

    public Score applyAbsence() {
        return new Score(value + ABSENCE);
    }

    public Score updateAttendanceToLate() {
        return new Score(value - ATTENDANCE + LATE);
    }

    public Score updateAttendanceToAbsence() {
        return new Score(value - ATTENDANCE + ABSENCE);
    }

    public Score updateLateToAttendance() {
        return new Score(value - LATE + ATTENDANCE);
    }

    public Score updateLateToAbsence() {
        return new Score(value - LATE + ABSENCE);
    }

    public Score updateAbsenceToAttendance() {
        return new Score(value - ABSENCE + ATTENDANCE);
    }

    public Score updateAbsenceToLate() {
        return new Score(value - ABSENCE + LATE);
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
