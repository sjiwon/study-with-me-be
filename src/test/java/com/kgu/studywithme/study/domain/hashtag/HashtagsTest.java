package com.kgu.studywithme.study.domain.hashtag;

import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.exception.StudyErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Stream;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Study -> 도메인 [Hashtags VO] 테스트")
class HashtagsTest {
    private final Member host = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final Study study = SPRING.toOnlineStudy(host.getId()).apply(1L, LocalDateTime.now());

    @Test
    @DisplayName("해시태그는 적어도 1개 이상 존재해야 한다")
    void throwExceptionByHashtagMustExistsAtLeastOne() {
        assertThatThrownBy(() -> new Hashtags(study, Set.of()))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.HASHTAG_MUST_EXISTS_AT_LEAST_ONE.getMessage());
    }

    @Test
    @DisplayName("해시태그는 5개 이상 존재할 수 없다")
    void throwExceptionByHashtagMustNotExistsMoreThanFive() {
        assertThatThrownBy(() -> new Hashtags(study, Set.of("A", "B", "C", "D", "E", "F")))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(StudyErrorCode.HASHTAG_MUST_NOT_EXISTS_MORE_THAN_FIVE.getMessage());
    }

    @ParameterizedTest
    @MethodSource("hashtags")
    @DisplayName("Hashtags를 생성한다")
    void success(final Set<String> hashtags) {
        assertDoesNotThrow(() -> new Hashtags(study, hashtags));
    }

    private static Stream<Arguments> hashtags() {
        return Stream.of(
                Arguments.of(Set.of("A")),
                Arguments.of(Set.of("A", "B", "C")),
                Arguments.of(Set.of("A", "B", "C", "D", "E"))
        );
    }
}
