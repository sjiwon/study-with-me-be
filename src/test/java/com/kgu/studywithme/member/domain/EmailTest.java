package com.kgu.studywithme.member.domain;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Member -> 도메인 [Email VO] 테스트")
class EmailTest {
    @ParameterizedTest
    @ValueSource(strings = {"", "abc", "@gmail.com", "abc@gmail", "abc@naver.com", "abc@kakao.com"})
    @DisplayName("형식에 맞지 않는 Email이면 생성에 실패한다")
    void throwExceptionByInvalidEmailFormat(final String value) {
        assertThatThrownBy(() -> Email.from(value))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.INVALID_EMAIL_FORMAT.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"test1@gmail.com", "test2@gmail.com"})
    @DisplayName("Email을 생성한다")
    void construct(final String value) {
        assertDoesNotThrow(() -> Email.from(value));
    }
}
