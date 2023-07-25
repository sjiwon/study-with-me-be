package com.kgu.studywithme.study.domain;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Description {
    @Lob
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String value;

    public Description(final String value) {
        validateDescriptionIsNotBlank(value);
        this.value = value;
    }

    private void validateDescriptionIsNotBlank(final String value) {
        if (value.isBlank()) {
            throw StudyWithMeException.type(StudyErrorCode.DESCRIPTION_IS_BLANK);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Description other = (Description) o;

        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
