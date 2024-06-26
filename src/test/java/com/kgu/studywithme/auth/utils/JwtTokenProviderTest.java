package com.kgu.studywithme.auth.utils;

import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.common.ParallelTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Auth -> JwtTokenProvider 테스트")
class JwtTokenProviderTest extends ParallelTest {
    private static final String SECRET_KEY = "asldfjsadlfjalksjf01jf02j9012f0120f12jf1j29v0saduf012ue101212c01";

    private final JwtTokenProvider invalidProvider = new JwtTokenProvider(SECRET_KEY, 0L, 0L);
    private final JwtTokenProvider validProvider = new JwtTokenProvider(SECRET_KEY, 7200L, 7200L);

    private final Long memberId = 1L;

    @Test
    @DisplayName("AccessToken과 RefreshToken을 발급한다")
    void issueAccessTokenAndRefreshToken() {
        // when
        final String accessToken = validProvider.createAccessToken(memberId);
        final String refreshToken = validProvider.createRefreshToken(memberId);

        // then
        assertAll(
                () -> assertThat(accessToken).isNotNull(),
                () -> assertThat(refreshToken).isNotNull()
        );
    }

    @Test
    @DisplayName("Token의 Payload를 추출한다")
    void extractTokenPayload() {
        // when
        final String accessToken = validProvider.createAccessToken(memberId);

        // then
        assertThat(validProvider.getId(accessToken)).isEqualTo(memberId);
    }

    @Test
    @DisplayName("Token 만료에 대한 유효성을 검증한다")
    void validateTokenExpire() {
        // when
        final String validToken = validProvider.createAccessToken(memberId);
        final String invalidToken = invalidProvider.createAccessToken(memberId);

        // then
        assertDoesNotThrow(() -> validProvider.validateToken(validToken));
        assertThatThrownBy(() -> invalidProvider.validateToken(invalidToken))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(AuthErrorCode.INVALID_TOKEN.getMessage());
    }

    @Test
    @DisplayName("Token 조작에 대한 유효성을 검증한다")
    void validateTokenManipulation() {
        // when
        final String forgedToken = validProvider.createAccessToken(memberId) + "hacked";

        // then
        assertThatThrownBy(() -> validProvider.validateToken(forgedToken))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(AuthErrorCode.INVALID_TOKEN.getMessage());
    }
}
