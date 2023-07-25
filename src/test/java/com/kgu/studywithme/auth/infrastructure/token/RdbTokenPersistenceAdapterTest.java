package com.kgu.studywithme.auth.infrastructure.token;

import com.kgu.studywithme.auth.domain.Token;
import com.kgu.studywithme.auth.domain.TokenRepository;
import com.kgu.studywithme.common.RepositoryTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static com.kgu.studywithme.common.utils.TokenUtils.REFRESH_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(RdbTokenPersistenceAdapter.class)
@DisplayName("Auth -> RdbTokenPersistenceAdapter 테스트")
class RdbTokenPersistenceAdapterTest extends RepositoryTest {
    @Autowired
    private RdbTokenPersistenceAdapter rdbTokenPersistenceAdapter;

    @Autowired
    private TokenRepository tokenRepository;

    private static final Long MEMBER_ID = 1L;

    @Nested
    @DisplayName("RefreshToken 동기화")
    class synchronizedRefreshToken {
        @Test
        @DisplayName("RefreshToken을 보유하고 있지 않은 사용자에게는 새로운 RefreshToken을 발급한다")
        void reissueRefreshToken() {
            // when
            rdbTokenPersistenceAdapter.synchronizeRefreshToken(MEMBER_ID, REFRESH_TOKEN);

            // then
            final Token findToken = tokenRepository.findByMemberId(MEMBER_ID).orElseThrow();
            assertThat(findToken.getRefreshToken()).isEqualTo(REFRESH_TOKEN);
        }

        @Test
        @DisplayName("RefreshToken을 보유하고 있는 사용자에게는 새로운 RefreshToken으로 업데이트한다")
        void updateRefreshToken() {
            // given
            tokenRepository.save(Token.issueRefreshToken(MEMBER_ID, REFRESH_TOKEN));

            // when
            final String newRefreshToken = REFRESH_TOKEN + "new";
            rdbTokenPersistenceAdapter.synchronizeRefreshToken(MEMBER_ID, newRefreshToken);

            // then
            final Token findToken = tokenRepository.findByMemberId(MEMBER_ID).orElseThrow();
            assertThat(findToken.getRefreshToken()).isEqualTo(newRefreshToken);
        }
    }

    @Test
    @DisplayName("RTR정책에 의해서 RefreshToken을 재발급한다")
    void reissueRefreshTokenByRtrPolicy() {
        // given
        tokenRepository.save(Token.issueRefreshToken(MEMBER_ID, REFRESH_TOKEN));

        // when
        final String newRefreshToken = REFRESH_TOKEN + "new";
        rdbTokenPersistenceAdapter.reissueRefreshTokenByRtrPolicy(MEMBER_ID, newRefreshToken);

        // then
        final Token findToken = tokenRepository.findByMemberId(MEMBER_ID).orElseThrow();
        assertThat(findToken.getRefreshToken()).isEqualTo(newRefreshToken);
    }

    @Test
    @DisplayName("사용자가 보유하고 있는 RefreshToken을 삭제한다")
    void deleteRefreshTokenByMemberId() {
        // given
        tokenRepository.save(Token.issueRefreshToken(MEMBER_ID, REFRESH_TOKEN));

        // when
        rdbTokenPersistenceAdapter.deleteRefreshTokenByMemberId(MEMBER_ID);

        // then
        assertThat(tokenRepository.findByMemberId(MEMBER_ID)).isEmpty();
    }

    @Test
    @DisplayName("사용자가 보유하고 있는 RefreshToken인지 확인한다")
    void isRefreshTokenExists() {
        // given
        tokenRepository.save(Token.issueRefreshToken(MEMBER_ID, REFRESH_TOKEN));

        // when
        final boolean actual1 = rdbTokenPersistenceAdapter.isRefreshTokenExists(MEMBER_ID, REFRESH_TOKEN);
        final boolean actual2 = rdbTokenPersistenceAdapter.isRefreshTokenExists(MEMBER_ID, REFRESH_TOKEN + "fake");

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}
