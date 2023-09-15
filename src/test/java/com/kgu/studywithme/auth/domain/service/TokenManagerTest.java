package com.kgu.studywithme.auth.domain.service;

import com.kgu.studywithme.auth.application.adapter.TokenPersistenceAdapter;
import com.kgu.studywithme.auth.domain.model.AuthToken;
import com.kgu.studywithme.auth.utils.TokenProvider;
import com.kgu.studywithme.common.mock.fake.FakeTokenPersistenceAdapter;
import com.kgu.studywithme.common.mock.stub.StubTokenProvider;
import com.kgu.studywithme.member.domain.model.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.kgu.studywithme.common.utils.TokenUtils.REFRESH_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Auth -> TokenManager 테스트")
public class TokenManagerTest {
    private final TokenProvider tokenProvider = new StubTokenProvider();
    private final TokenPersistenceAdapter tokenPersistenceAdapter = new FakeTokenPersistenceAdapter();
    private final TokenManager sut = new TokenManager(tokenProvider, tokenPersistenceAdapter);
    private final Member member = JIWON.toMember().apply(1L);

    @Test
    @DisplayName("Token[Access + Refresh]를 제공한다")
    void provideAuthorityToken() {
        // when
        final AuthToken authToken = sut.provideAuthorityToken(member.getId());

        // then
        assertAll(
                () -> assertThat(authToken.accessToken()).isEqualTo(ACCESS_TOKEN),
                () -> assertThat(authToken.refreshToken()).isEqualTo(REFRESH_TOKEN),
                () -> assertThat(tokenPersistenceAdapter.isMemberRefreshToken(member.getId(), REFRESH_TOKEN)).isTrue()
        );
    }

    @Test
    @DisplayName("Token[Access + Refresh]를 재발급한다")
    void reissueAuthorityToken() {
        // given
        tokenPersistenceAdapter.synchronizeRefreshToken(member.getId(), REFRESH_TOKEN);

        // when
        final AuthToken authToken = sut.reissueAuthorityToken(member.getId());

        // then
        assertAll(
                () -> assertThat(authToken.accessToken()).isEqualTo(ACCESS_TOKEN),
                () -> assertThat(authToken.refreshToken()).isEqualTo(REFRESH_TOKEN),
                () -> assertThat(tokenPersistenceAdapter.isMemberRefreshToken(member.getId(), REFRESH_TOKEN)).isTrue()
        );
    }

    @Test
    @DisplayName("사용자의 RefreshToken인지 확인한다")
    void isMemberRefreshToken() {
        // given
        tokenPersistenceAdapter.synchronizeRefreshToken(member.getId(), REFRESH_TOKEN);

        // when
        final boolean actual1 = sut.isMemberRefreshToken(member.getId(), REFRESH_TOKEN);
        final boolean actual2 = sut.isMemberRefreshToken(member.getId(), REFRESH_TOKEN + "X");

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("사용자의 RefreshToken을 제거한다")
    void deleteMemberRefreshToken() {
        // given
        tokenPersistenceAdapter.synchronizeRefreshToken(member.getId(), REFRESH_TOKEN);

        // when
        sut.deleteMemberRefreshToken(member.getId());

        // then
        assertThat(tokenPersistenceAdapter.isMemberRefreshToken(member.getId(), REFRESH_TOKEN)).isFalse();
    }
}
