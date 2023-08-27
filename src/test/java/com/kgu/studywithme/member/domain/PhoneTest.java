package com.kgu.studywithme.member.domain;

import com.kgu.studywithme.common.ExecuteParallel;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExecuteParallel
@DisplayName("Member -> 도메인 [Phone VO] 테스트")
public class PhoneTest {
    @ParameterizedTest
    @ValueSource(strings = {"01012345678", "010-12345678", "0101234-5678", "010-12-3456", "010-123-456", "01-234-5678"})
    @DisplayName("형식에 맞지 않는 Phone이면 생성에 실패한다")
    void throwExceptionByInvalidPhoneFormat(final String value) {
        assertThatThrownBy(() -> new Phone(value))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.INVALID_PHONE_PATTERN.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"010-1234-5678", "010-123-4567"})
    @DisplayName("Phone을 생성한다")
    void construct(final String value) {
        assertDoesNotThrow(() -> new Phone(value));
    }
}
