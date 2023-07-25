package com.kgu.studywithme.auth.application.service;

import com.kgu.studywithme.auth.application.dto.TokenResponse;
import com.kgu.studywithme.auth.application.usecase.command.ReissueTokenUseCase;
import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.auth.infrastructure.token.TokenPersistenceAdapter;
import com.kgu.studywithme.auth.utils.JwtTokenProvider;
import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static com.kgu.studywithme.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.kgu.studywithme.common.utils.TokenUtils.REFRESH_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Auth -> ReissueTokenService 테스트")
class ReissueTokenServiceTest extends UseCaseTest {
    @InjectMocks
    private ReissueTokenService tokenReissueService;

    @Mock
    private TokenPersistenceAdapter tokenPersistenceAdapter;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private final ReissueTokenUseCase.Command command =
            new ReissueTokenUseCase.Command(
                    1L,
                    REFRESH_TOKEN
            );

    @Nested
    @DisplayName("토큰 재발급")
    class reissueToken {
        @Test
        @DisplayName("사용자 소유의 RefreshToken이 아니면 재발급을 할 수 없다")
        void throwExceptionByInvalidRefreshToken() {
            // given
            given(tokenPersistenceAdapter.isRefreshTokenExists(any(), any())).willReturn(false);

            // when - then
            assertThatThrownBy(() -> tokenReissueService.reissueToken(command))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(AuthErrorCode.INVALID_TOKEN.getMessage());

            assertAll(
                    () -> verify(tokenPersistenceAdapter, times(1)).isRefreshTokenExists(any(), any()),
                    () -> verify(jwtTokenProvider, times(0)).createAccessToken(any()),
                    () -> verify(jwtTokenProvider, times(0)).createRefreshToken(any()),
                    () -> verify(tokenPersistenceAdapter, times(0)).reissueRefreshTokenByRtrPolicy(any(), any())
            );
        }

        @Test
        @DisplayName("사용자 소유의 RefreshToken을 통해서 AccessToken과 RefreshToken을 재발급받는다")
        void success() {
            // given
            given(tokenPersistenceAdapter.isRefreshTokenExists(any(), any())).willReturn(true);
            given(jwtTokenProvider.createAccessToken(any())).willReturn(ACCESS_TOKEN);
            given(jwtTokenProvider.createRefreshToken(any())).willReturn(REFRESH_TOKEN);

            // when
            final TokenResponse response = tokenReissueService.reissueToken(command);

            // then
            assertAll(
                    () -> verify(tokenPersistenceAdapter, times(1)).isRefreshTokenExists(any(), any()),
                    () -> verify(jwtTokenProvider, times(1)).createAccessToken(any()),
                    () -> verify(jwtTokenProvider, times(1)).createRefreshToken(any()),
                    () -> verify(tokenPersistenceAdapter, times(1)).reissueRefreshTokenByRtrPolicy(any(), any()),
                    () -> assertThat(response.accessToken()).isEqualTo(ACCESS_TOKEN),
                    () -> assertThat(response.refreshToken()).isEqualTo(REFRESH_TOKEN)
            );
        }
    }
}
