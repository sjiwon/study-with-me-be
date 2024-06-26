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
public class Email {
    /**
     * 이메일 형식은 [@gmail.com, @naver.com, @kakao.com]만 허용
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@(gmail\\.com|naver\\.com|kakao\\.com)$");

    @Column(name = "email", nullable = false, unique = true, updatable = false)
    private String value;

    @Column(name = "is_email_opt_in", nullable = false)
    private boolean emailOptIn;

    public Email(final String value, final boolean emailOptIn) {
        validateEmailPattern(value);
        this.value = value;
        this.emailOptIn = emailOptIn;
    }

    private void validateEmailPattern(final String value) {
        if (isNotValidPattern(value)) {
            throw StudyWithMeException.type(MemberErrorCode.INVALID_EMAIL_PATTERN);
        }
    }

    private boolean isNotValidPattern(final String value) {
        return !EMAIL_PATTERN.matcher(value).matches();
    }

    public Email updateEmailOptIn(final boolean emailOptIn) {
        return new Email(value, emailOptIn);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Email other = (Email) o;

        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
