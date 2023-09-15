package com.kgu.studywithme.auth.application.usecase;

import com.kgu.studywithme.auth.application.usecase.command.ReissueTokenCommand;
import com.kgu.studywithme.auth.domain.model.AuthToken;
import com.kgu.studywithme.auth.domain.service.TokenManager;
import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.kgu.studywithme.common.utils.TokenUtils.REFRESH_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Auth -> ReissueTokenUseCase 테스트")
class ReissueTokenUseCaseTest extends UseCaseTest {
    private final TokenManager tokenManager = mock(TokenManager.class);
    private final ReissueTokenUseCase sut = new ReissueTokenUseCase(tokenManager);

    private final ReissueTokenCommand command = new ReissueTokenCommand(1L, REFRESH_TOKEN);

    @Nested
    @DisplayName("토큰 재발급")
    class ReissueToken {
        @Test
        @DisplayName("사용자 소유의 RefreshToken이 아니면 재발급을 할 수 없다")
        void throwExceptionByInvalidRefreshToken() {
            // given
            given(tokenManager.isMemberRefreshToken(1L, REFRESH_TOKEN)).willReturn(false);

            // when - then
            assertThatThrownBy(() -> sut.invoke(command))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(AuthErrorCode.INVALID_TOKEN.getMessage());

            assertAll(
                    () -> verify(tokenManager, times(1)).isMemberRefreshToken(1L, REFRESH_TOKEN),
                    () -> verify(tokenManager, times(0)).reissueAuthorityToken(1L)
            );
        }

        @Test
        @DisplayName("사용자 소유의 RefreshToken을 통해서 AccessToken과 RefreshToken을 재발급받는다")
        void success() {
            // given
            given(tokenManager.isMemberRefreshToken(1L, REFRESH_TOKEN)).willReturn(true);
            given(tokenManager.reissueAuthorityToken(1L)).willReturn(new AuthToken(ACCESS_TOKEN, REFRESH_TOKEN));

            // when
            final AuthToken response = sut.invoke(command);

            // then
            assertAll(
                    () -> verify(tokenManager, times(1)).isMemberRefreshToken(1L, REFRESH_TOKEN),
                    () -> verify(tokenManager, times(1)).reissueAuthorityToken(1L),
                    () -> assertThat(response.accessToken()).isEqualTo(ACCESS_TOKEN),
                    () -> assertThat(response.refreshToken()).isEqualTo(REFRESH_TOKEN)
            );
        }
    }
}
