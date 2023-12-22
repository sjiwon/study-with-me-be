package com.kgu.studywithme.auth.application.usecase;

import com.kgu.studywithme.auth.application.usecase.command.ReissueTokenCommand;
import com.kgu.studywithme.auth.domain.model.AuthToken;
import com.kgu.studywithme.auth.domain.service.TokenIssuer;
import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.auth.utils.TokenProvider;
import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
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
    private final TokenProvider tokenProvider = mock(TokenProvider.class);
    private final TokenIssuer tokenIssuer = mock(TokenIssuer.class);
    private final ReissueTokenUseCase sut = new ReissueTokenUseCase(tokenProvider, tokenIssuer);

    private final Member member = JIWON.toMember().apply(1L);
    private final ReissueTokenCommand command = new ReissueTokenCommand(REFRESH_TOKEN);

    @Test
    @DisplayName("RefreshToken이 유효하지 않으면 토큰 재발급에 실패한다")
    void throwExceptionByInvalidRefreshToken() {
        // given
        given(tokenProvider.getId(command.refreshToken())).willReturn(member.getId());
        given(tokenIssuer.isMemberRefreshToken(member.getId(), command.refreshToken())).willReturn(false);

        // when - then
        assertThatThrownBy(() -> sut.invoke(command))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(AuthErrorCode.INVALID_TOKEN.getMessage());

        assertAll(
                () -> verify(tokenProvider, times(1)).getId(command.refreshToken()),
                () -> verify(tokenIssuer, times(1)).isMemberRefreshToken(member.getId(), command.refreshToken())
        );
    }

    @Test
    @DisplayName("유효성이 확인된 RefreshToken을 통해서 새로운 AccessToken과 RefreshToken을 재발급받는다")
    void reissueSuccess() {
        // given
        given(tokenProvider.getId(command.refreshToken())).willReturn(member.getId());
        given(tokenIssuer.isMemberRefreshToken(member.getId(), command.refreshToken())).willReturn(true);

        final AuthToken authToken = new AuthToken(ACCESS_TOKEN, REFRESH_TOKEN);
        given(tokenIssuer.reissueAuthorityToken(member.getId())).willReturn(authToken);

        // when
        final AuthToken result = sut.invoke(command);

        // then
        assertAll(
                () -> verify(tokenProvider, times(1)).getId(command.refreshToken()),
                () -> verify(tokenIssuer, times(1)).isMemberRefreshToken(member.getId(), command.refreshToken()),
                () -> assertThat(result.accessToken()).isEqualTo(ACCESS_TOKEN),
                () -> assertThat(result.refreshToken()).isEqualTo(REFRESH_TOKEN)
        );
    }
}
