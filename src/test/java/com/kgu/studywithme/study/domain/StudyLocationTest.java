package com.kgu.studywithme.study.domain;

import com.kgu.studywithme.common.ExecuteParallel;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExecuteParallel
@DisplayName("Study -> 도메인 [StudyLocation VO] 테스트")
class StudyLocationTest {
    @ParameterizedTest
    @MethodSource("invalidLocation")
    @DisplayName("province나 city가 비어있음에 따라 StudyLocation 생성에 실패한다")
    void throwExceptionByStudyLocationIsBlank(final String province, final String city) {
        assertThatThrownBy(() -> new StudyLocation(province, city))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.STUDY_LOCATION_IS_BLANK.getMessage());
    }

    private static Stream<Arguments> invalidLocation() {
        return Stream.of(
                Arguments.of("경기도", ""),
                Arguments.of("", "안양시"),
                Arguments.of("", "")
        );
    }

    @ParameterizedTest
    @MethodSource("validLocation")
    @DisplayName("StudyLocation[province / city]를 생성한다")
    void construct(final String province, final String city) {
        assertDoesNotThrow(() -> new StudyLocation(province, city));
    }

    private static Stream<Arguments> validLocation() {
        return Stream.of(
                Arguments.of("경기도", "안양시"),
                Arguments.of("경기도", "수원시"),
                Arguments.of("경기도", "성남시")
        );
    }
}
