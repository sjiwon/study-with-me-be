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

    private Capacity(final int value) {
        this.value = value;
    }

    public static Capacity from(final int value) {
        validateCapacityIsInRange(value);
        return new Capacity(value);
    }

    private static void validateCapacityIsInRange(final int value) {
        if (isOutOfRange(value)) {
            throw StudyWithMeException.type(StudyErrorCode.CAPACITY_OUT_OF_RANGE);
        }
    }

    private static boolean isOutOfRange(final int capacity) {
        return (capacity < MINIMUM) || (MAXIMUM < capacity);
    }

    public boolean isFullByCompareWith(final int compareValue) {
        return value <= compareValue;
    }
}
