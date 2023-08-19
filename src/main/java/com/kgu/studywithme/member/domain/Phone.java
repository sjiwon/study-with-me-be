package com.kgu.studywithme.member.domain;


import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Phone {
    /**
     * xxx-xxxx-xxxx 형태
     */
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{3}-\\d{3,4}-\\d{4}$");

    @Column(name = "phone", nullable = false, unique = true)
    private String value;

    public Phone(final String value) {
        validatePhonePattern(value);
        this.value = value;
    }

    private void validatePhonePattern(final String value) {
        if (isNotValidPattern(value)) {
            throw StudyWithMeException.type(MemberErrorCode.INVALID_PHONE_PATTERN);
        }
    }

    private boolean isNotValidPattern(final String value) {
        return !PHONE_PATTERN.matcher(value).matches();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Phone other = (Phone) o;

        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
