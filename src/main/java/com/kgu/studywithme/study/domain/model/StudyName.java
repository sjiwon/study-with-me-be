package com.kgu.studywithme.study.domain.model;

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
public class StudyName {
    private static final int MAXIMUM_LENGTH = 20;

    @Column(name = "name", nullable = false, unique = true)
    private String value;

    public StudyName(final String value) {
        validateNameIsNotBlank(value);
        validateLengthIsInRange(value);
        this.value = value;
    }

    private void validateNameIsNotBlank(final String value) {
        if (value.isBlank()) {
            throw StudyWithMeException.type(StudyErrorCode.NAME_IS_BLANK);
        }
    }

    private void validateLengthIsInRange(final String value) {
        if (isLengthOutOfRange(value)) {
            throw StudyWithMeException.type(StudyErrorCode.NAME_LENGTH_IS_OUT_OF_RANGE);
        }
    }

    private boolean isLengthOutOfRange(final String name) {
        return MAXIMUM_LENGTH < name.length();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final StudyName other = (StudyName) o;

        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
