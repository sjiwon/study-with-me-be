package com.kgu.studywithme.auth.domain.service;

import com.kgu.studywithme.auth.domain.model.Token;
import com.kgu.studywithme.auth.domain.repository.TokenRepository;
import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.member.domain.model.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Token -> TokenManager 테스트")
class TokenManagerTest extends UseCaseTest {
    private final TokenRepository tokenRepository = mock(TokenRepository.class);
    private final TokenManager sut = new TokenManager(tokenRepository);

    private final Member member = JIWON.toMember().apply(1L);
    private final String previousToken = "previous";
    private final String newToken = "new";

    @Nested
    @DisplayName("RefreshToken 동기화")
    class SynchronizeRefreshToken {
        @Test
        @DisplayName("RefreshToken을 보유한 상태면 새로운 RefreshToken으로 대체한다")
        void update() {
            // given
            final Token token = Token.issueRefreshToken(member.getId(), previousToken).apply(1L);
            given(tokenRepository.findByMemberId(member.getId())).willReturn(Optional.of(token));

            // when
            sut.synchronizeRefreshToken(member.getId(), newToken);

            // then
            assertAll(
                    () -> verify(tokenRepository, times(0)).save(any(Token.class)),
                    () -> assertThat(token.getRefreshToken()).isEqualTo(newToken)
            );
        }

        @Test
        @DisplayName("보유한 RefreshToken이 없다면 새로 발급한다")
        void reissue() {
            // given
            given(tokenRepository.findByMemberId(member.getId())).willReturn(Optional.empty());

            // when
            sut.synchronizeRefreshToken(member.getId(), newToken);

            // then
            verify(tokenRepository, times(1)).save(any(Token.class));
        }
    }

    @Test
    @DisplayName("사용자 소유의 RefreshToken인지 확인한다")
    void isMemberRefreshToken() {
        // given
        given(tokenRepository.existsByMemberIdAndRefreshToken(member.getId(), previousToken)).willReturn(false);
        given(tokenRepository.existsByMemberIdAndRefreshToken(member.getId(), newToken)).willReturn(true);

        // when
        final boolean actual1 = sut.isMemberRefreshToken(member.getId(), previousToken);
        final boolean actual2 = sut.isMemberRefreshToken(member.getId(), newToken);

        // then
        assertAll(
                () -> assertThat(actual1).isFalse(),
                () -> assertThat(actual2).isTrue()
        );
    }
}
