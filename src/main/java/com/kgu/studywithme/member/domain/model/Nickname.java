package com.kgu.studywithme.member.domain.model;

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
public class Nickname {
    /**
     * 1. 한글 & 알파벳 대소문자 & 숫자 가능 <br>
     * 2. 공백 불가능 <br>
     * 3. 2자 이상 10자 이하
     */
    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[a-zA-Z가-힣0-9]{2,10}$");

    @Column(name = "nickname", nullable = false, unique = true)
    private String value;

    public Nickname(final String value) {
        validateNicknamePattern(value);
        this.value = value;
    }

    public Nickname update(final String value) {
        validateNicknamePattern(value);
        validateNicknameIsSameAsBefore(value);
        return new Nickname(value);
    }

    private void validateNicknamePattern(final String value) {
        if (isInvalidPattern(value)) {
            throw StudyWithMeException.type(MemberErrorCode.INVALID_NICKNAME_PATTERN);
        }
    }

    private boolean isInvalidPattern(final String nickname) {
        return !NICKNAME_PATTERN.matcher(nickname).matches();
    }

    private void validateNicknameIsSameAsBefore(final String value) {
        if (this.value.equals(value)) {
            throw StudyWithMeException.type(MemberErrorCode.NICKNAME_SAME_AS_BEFORE);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Nickname other = (Nickname) o;

        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
