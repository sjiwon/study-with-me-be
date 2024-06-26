package com.kgu.studywithme.auth.utils;

import com.kgu.studywithme.common.ParallelTest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.kgu.studywithme.auth.utils.TokenResponseWriter.REFRESH_TOKEN_COOKIE;
import static com.kgu.studywithme.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.kgu.studywithme.common.utils.TokenUtils.BEARER_TOKEN;
import static com.kgu.studywithme.common.utils.TokenUtils.REFRESH_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@DisplayName("Auth -> RequestTokenExtractor 테스트")
class RequestTokenExtractorTest extends ParallelTest {
    private final HttpServletRequest request = mock(HttpServletRequest.class);

    @Nested
    @DisplayName("AccessToken 추출")
    class ExtractAccessToken {
        @Test
        @DisplayName("HTTP Request Message의 Authorization Header에 토큰이 없다면 Optional 빈 값을 응답한다")
        void emptyToken() {
            // given
            given(request.getHeader(AUTHORIZATION)).willReturn(null);

            // when
            final Optional<String> token = RequestTokenExtractor.extractAccessToken(request);

            // then
            assertThat(token).isEmpty();
        }

        @Test
        @DisplayName("HTTP Request Message의 Authorization Header에 토큰 타입만 명시되었다면 Optional 빈 값을 응답한다")
        void emptyTokenWithType() {
            // given
            given(request.getHeader(AUTHORIZATION)).willReturn(BEARER_TOKEN);

            // when
            final Optional<String> token = RequestTokenExtractor.extractAccessToken(request);

            // then
            assertThat(token).isEmpty();
        }

        @Test
        @DisplayName("HTTP Request Message의 Authorization Header에 토큰이 있다면 Optional로 감싸서 응답한다")
        void success() {
            // given
            given(request.getHeader(AUTHORIZATION)).willReturn(String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // when
            final Optional<String> token = RequestTokenExtractor.extractAccessToken(request);

            // then
            assertAll(
                    () -> assertThat(token).isPresent(),
                    () -> assertThat(token.get()).isEqualTo(ACCESS_TOKEN)
            );
        }
    }

    @Nested
    @DisplayName("RefreshToken 추출")
    class ExtractRefreshToken {
        @Test
        @DisplayName("HTTP Request Message의 Cookie에 RefreshToken이 없다면 Optional 빈 값을 응답한다")
        void emptyToken() {
            // given
            given(request.getCookies()).willReturn(new Cookie[0]);

            // when
            final Optional<String> token = RequestTokenExtractor.extractRefreshToken(request);

            // then
            assertThat(token).isEmpty();
        }

        @Test
        @DisplayName("HTTP Request Message의 Cookie에 RefreshToken이 있다면 Optional로 감싸서 응답한다")
        void success() {
            // given
            given(request.getCookies()).willReturn(new Cookie[]{new Cookie(REFRESH_TOKEN_COOKIE, REFRESH_TOKEN)});

            // when
            final Optional<String> token = RequestTokenExtractor.extractRefreshToken(request);

            // then
            assertAll(
                    () -> assertThat(token).isPresent(),
                    () -> assertThat(token.get()).isEqualTo(REFRESH_TOKEN)
            );
        }
    }
}
