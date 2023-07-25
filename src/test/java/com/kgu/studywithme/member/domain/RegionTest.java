package com.kgu.studywithme.member.domain;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Member -> 도메인 [Region VO] 테스트")
class RegionTest {
    @ParameterizedTest
    @MethodSource("invalidRegion")
    @DisplayName("province나 city가 비어있음에 따라 Region 생성에 실패한다")
    void throwExceptionByRegionIsBlank(final String province, final String city) {
        assertThatThrownBy(() -> new Region(province, city))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.REGION_IS_BLANK.getMessage());
    }

    private static Stream<Arguments> invalidRegion() {
        return Stream.of(
                Arguments.of("경기도", ""),
                Arguments.of("", "수원시"),
                Arguments.of("", "")
        );
    }

    @ParameterizedTest
    @MethodSource("validRegion")
    @DisplayName("Region[province / city]을 생성한다")
    void construct(final String province, final String city) {
        assertDoesNotThrow(() -> new Region(province, city));
    }

    private static Stream<Arguments> validRegion() {
        return Stream.of(
                Arguments.of("경기도", "안양시"),
                Arguments.of("경기도", "수원시"),
                Arguments.of("경기도", "성남시")
        );
    }

    @Test
    @DisplayName("Region을 수정한다")
    void update() {
        // given
        final Region region = new Region("경기도", "안양시");

        // when
        final Region updateRegion = region.update("경기도", "수원시");

        // then
        assertAll(
                () -> assertThat(updateRegion.getProvince()).isEqualTo("경기도"),
                () -> assertThat(updateRegion.getCity()).isEqualTo("수원시")
        );
    }
}
