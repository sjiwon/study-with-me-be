package com.kgu.studywithme.member.domain.interest;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Stream;

import static com.kgu.studywithme.category.domain.Category.*;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Member -> 도메인 [Interests VO] 테스트")
class InterestsTest {
    private final Member member = JIWON.toMember().apply(1L, LocalDateTime.now());

    @Nested
    @DisplayName("Interests 생성")
    class Construct {
        @Test
        @DisplayName("관심사는 적어도 1개 이상 존재해야 한다")
        void throwExceptionByInterestMustExistsAtLeastOne() {
            assertThatThrownBy(() -> new Interests(member, Set.of()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberErrorCode.INTEREST_MUST_EXISTS_AT_LEAST_ONE.getMessage());
        }

        @ParameterizedTest
        @MethodSource("interests")
        @DisplayName("Interests를 생성한다")
        void construct(final Set<Category> interests) {
            assertDoesNotThrow(() -> new Interests(member, interests));
        }

        private static Stream<Arguments> interests() {
            return Stream.of(
                    Arguments.of(Set.of(INTERVIEW)),
                    Arguments.of(Set.of(INTERVIEW, PROGRAMMING))
            );
        }
    }

    @Nested
    @DisplayName("Interests 업데이트")
    class Upate {
        private Interests interests;

        @BeforeEach
        void setUp() {
            interests = new Interests(member, Set.of(INTERVIEW, PROGRAMMING));
        }

        @Test
        @DisplayName("관심사는 적어도 1개 이상 존재해야 한다")
        void throwExceptionByInterestMustExistsAtLeastOne() {
            assertThatThrownBy(() -> interests.update(member, Set.of()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberErrorCode.INTEREST_MUST_EXISTS_AT_LEAST_ONE.getMessage());
        }

        @Test
        @DisplayName("Interests를 업데이트한다")
        void success() {
            // when
            interests.update(member, Set.of(INTERVIEW, PROGRAMMING, APTITUDE_NCS, ETC));

            // then
            assertAll(
                    () -> assertThat(interests.getInterests()).hasSize(4),
                    () -> assertThat(interests.getInterests())
                            .map(Interest::getCategory)
                            .containsExactlyInAnyOrder(INTERVIEW, PROGRAMMING, APTITUDE_NCS, ETC)
            );
        }
    }
}
