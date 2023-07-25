package com.kgu.studywithme.study.domain;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Capacity {
    private static final int MINIMUM = 2;
    private static final int MAXIMUM = 10;

    @Column(name = "capacity", nullable = false)
    private int value;

    public Capacity(final int value) {
        validateCapacityIsInRange(value);
        this.value = value;
    }

    private void validateCapacityIsInRange(final int value) {
        if (isOutOfRange(value)) {
            throw StudyWithMeException.type(StudyErrorCode.CAPACITY_IS_OUT_OF_RANGE);
        }
    }

    private boolean isOutOfRange(final int capacity) {
        return (capacity < MINIMUM) || (MAXIMUM < capacity);
    }

    public boolean isEqualOrLessThan(final int compareValue) {
        return value <= compareValue;
    }

    public boolean isLessThan(final int compareValue) {
        return value < compareValue;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Capacity other = (Capacity) o;

        return value == other.value;
    }

    @Override
    public int hashCode() {
        return value;
    }
}
