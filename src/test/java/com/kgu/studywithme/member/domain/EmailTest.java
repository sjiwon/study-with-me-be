package com.kgu.studywithme.member.domain;

import com.kgu.studywithme.common.ExecuteParallel;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExecuteParallel
@DisplayName("Member -> 도메인 [Email VO] 테스트")
class EmailTest {
    @ParameterizedTest
    @ValueSource(strings = {"", "abc", "@gmail.com", "@naver.com", "@kakao.com", "abc@gmail", "abc@naver", "abc@kakao"})
    @DisplayName("형식에 맞지 않는 Email이면 생성에 실패한다")
    void throwExceptionByInvalidEmailFormat(final String value) {
        assertThatThrownBy(() -> new Email(value, true))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.INVALID_EMAIL_PATTERN.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"hello@gmail.com", "hello@naver.com", "hello@kakao.com"})
    @DisplayName("Email을 생성한다")
    void construct(final String value) {
        assertDoesNotThrow(() -> new Email(value, true));
    }

    @Test
    @DisplayName("이메일 수신 동의 여부를 수정한다")
    void updateEmailOptIn() {
        // given
        final Email email = new Email("hello@gmail.com", true);
        assertThat(email.isEmailOptIn()).isTrue();

        // when
        final Email updateEmail = email.updateEmailOptIn(false);

        // then
        assertThat(updateEmail.isEmailOptIn()).isFalse();
    }
}
