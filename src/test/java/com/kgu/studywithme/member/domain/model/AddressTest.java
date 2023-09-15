package com.kgu.studywithme.member.domain.model;

import com.kgu.studywithme.common.ParallelTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Member -> 도메인 [Address VO] 테스트")
class AddressTest extends ParallelTest {
    @ParameterizedTest
    @MethodSource("invalidAddress")
    @DisplayName("province나 city가 비어있음에 따라 Address 생성에 실패한다")
    void throwExceptionByAddressIsBlank(final String province, final String city) {
        assertThatThrownBy(() -> new Address(province, city))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.ADDRESS_IS_BLANK.getMessage());
    }

    private static Stream<Arguments> invalidAddress() {
        return Stream.of(
                Arguments.of("경기도", ""),
                Arguments.of("", "수원시"),
                Arguments.of("", "")
        );
    }

    @ParameterizedTest
    @MethodSource("validAddress")
    @DisplayName("Address[province / city]을 생성한다")
    void construct(final String province, final String city) {
        assertDoesNotThrow(() -> new Address(province, city));
    }

    private static Stream<Arguments> validAddress() {
        return Stream.of(
                Arguments.of("경기도", "안양시"),
                Arguments.of("경기도", "수원시"),
                Arguments.of("경기도", "성남시")
        );
    }
}
