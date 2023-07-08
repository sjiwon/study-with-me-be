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
public class Email {
    // 이메일 형식은 @gmail.com만 허용
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@gmail\\.com$";
    private static final Pattern EMAIL_MATCHER = Pattern.compile(EMAIL_PATTERN);

    @Column(name = "email", nullable = false, unique = true, updatable = false)
    private String value;

    private Email(final String value) {
        this.value = value;
    }

    public static Email from(final String value) {
        validateEmailPattern(value);
        return new Email(value);
    }

    private static void validateEmailPattern(final String value) {
        if (isNotValidPattern(value)) {
            throw StudyWithMeException.type(MemberErrorCode.INVALID_EMAIL_FORMAT);
        }
    }

    private static boolean isNotValidPattern(final String email) {
        return !EMAIL_MATCHER.matcher(email).matches();
    }

    public boolean isSameEmail(final Email email) {
        return this.value.equals(email.getValue());
    }
}
