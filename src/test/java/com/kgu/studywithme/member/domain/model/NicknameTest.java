package com.kgu.studywithme.member.domain.model;

import com.kgu.studywithme.common.ParallelTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Member -> 도메인 [Nickname VO] 테스트")
class NicknameTest extends ParallelTest {
    @ParameterizedTest
    @ValueSource(strings = {"한", "!@#hello", "Hello World", "일이삼사오육칠팔구십십일"})
    @DisplayName("형식에 맞지 않는 Nickname이면 생성에 실패한다")
    void throwExceptionByInvalidNicknameFormat(final String value) {
        assertThatThrownBy(() -> new Nickname(value))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(MemberErrorCode.INVALID_NICKNAME_PATTERN.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"하이", "하이123", "hEllo123"})
    @DisplayName("Nickname을 생성한다")
    void construct(final String value) {
        assertDoesNotThrow(() -> new Nickname(value));
    }

    @Nested
    @DisplayName("닉네임 수정")
    class Update {
        @Test
        @DisplayName("이전과 동일한 닉네임으로 수정할 수 없다")
        void throwExceptionByNicknameSameAsBefore() {
            // given
            final Nickname nickname = new Nickname("Hello");

            // when - then
            assertThatThrownBy(() -> nickname.update(nickname.getValue()))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(MemberErrorCode.NICKNAME_SAME_AS_BEFORE.getMessage());
        }

        @Test
        @DisplayName("닉네임을 수정한다")
        void success() {
            // given
            final Nickname nickname = new Nickname("Hello");

            // when
            final Nickname updateNickname = nickname.update("HelloA");

            // then
            assertThat(updateNickname.getValue()).isEqualTo("HelloA");
        }
    }
}
