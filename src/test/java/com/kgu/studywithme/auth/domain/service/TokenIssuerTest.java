package com.kgu.studywithme.auth.domain.service;

import com.kgu.studywithme.auth.domain.model.AuthToken;
import com.kgu.studywithme.auth.utils.TokenProvider;
import com.kgu.studywithme.common.ParallelTest;
import com.kgu.studywithme.common.mock.stub.StubTokenProvider;
import com.kgu.studywithme.member.domain.model.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.kgu.studywithme.common.utils.TokenUtils.REFRESH_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Auth -> TokenIssuer 테스트")
public class TokenIssuerTest extends ParallelTest {
    private final TokenProvider tokenProvider = new StubTokenProvider();
    private final TokenManager tokenManager = mock(TokenManager.class);
    private final TokenIssuer sut = new TokenIssuer(tokenProvider, tokenManager);

    private final Member member = JIWON.toMember().apply(1L);

    @Test
    @DisplayName("AuthToken[Access + Refresh]을 제공한다")
    void provideAuthorityToken() {
        // when
        final AuthToken authToken = sut.provideAuthorityToken(member.getId());

        // then
        assertAll(
                () -> verify(tokenManager, times(1)).synchronizeRefreshToken(member.getId(), REFRESH_TOKEN),
                () -> assertThat(authToken.accessToken()).isEqualTo(ACCESS_TOKEN),
                () -> assertThat(authToken.refreshToken()).isEqualTo(REFRESH_TOKEN)
        );
    }

    @Test
    @DisplayName("AuthToken[Access + Refresh]을 재발급한다")
    void reissueAuthorityToken() {
        // given
        tokenManager.synchronizeRefreshToken(member.getId(), REFRESH_TOKEN);

        // when
        final AuthToken authToken = sut.reissueAuthorityToken(member.getId());

        // then
        assertAll(
                () -> verify(tokenManager, times(1)).updateRefreshToken(member.getId(), REFRESH_TOKEN),
                () -> assertThat(authToken.accessToken()).isEqualTo(ACCESS_TOKEN),
                () -> assertThat(authToken.refreshToken()).isEqualTo(REFRESH_TOKEN)
        );
    }
}
