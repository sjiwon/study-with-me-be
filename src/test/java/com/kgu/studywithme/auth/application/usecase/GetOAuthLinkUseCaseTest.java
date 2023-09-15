package com.kgu.studywithme.auth.application.usecase;

import com.kgu.studywithme.auth.application.adapter.OAuthUriGenerator;
import com.kgu.studywithme.auth.application.usecase.query.OAuthLinkQuery;
import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.auth.infrastructure.oauth.google.GoogleOAuthUriGenerator;
import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.kgu.studywithme.auth.domain.model.oauth.OAuthProvider.GOOGLE;
import static com.kgu.studywithme.common.utils.OAuthUtils.REDIRECT_URI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Auth -> GetOAuthLinkUseCase 테스트")
class GetOAuthLinkUseCaseTest extends UseCaseTest {
    private final GoogleOAuthUriGenerator googleOAuthUriGenerator = mock(GoogleOAuthUriGenerator.class);
    private final List<OAuthUriGenerator> oAuthUriGenerators = List.of(googleOAuthUriGenerator);
    private final GetOAuthLinkUseCase sut = new GetOAuthLinkUseCase(oAuthUriGenerators);

    @Test
    @DisplayName("제공하지 않는 OAuth Provider에 대해서는 예외가 발생한다")
    void throwExceptionByInvalidOAuthProvider() {
        // given
        given(googleOAuthUriGenerator.isSupported(null)).willReturn(false);

        // when - then
        assertThatThrownBy(() -> sut.invoke(new OAuthLinkQuery(null, REDIRECT_URI)))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(AuthErrorCode.INVALID_OAUTH_PROVIDER.getMessage());

        verify(googleOAuthUriGenerator, times(0)).generate(REDIRECT_URI);
    }

    @Test
    @DisplayName("Google Provider에 대해서 생성된 OAuthUri를 응답받는다")
    void successGoogle() {
        // given
        given(googleOAuthUriGenerator.isSupported(GOOGLE)).willReturn(true);

        final String googleOAuthUri = "Google/" + REDIRECT_URI;
        given(googleOAuthUriGenerator.generate(REDIRECT_URI)).willReturn(googleOAuthUri);

        // when
        final String uri = sut.invoke(new OAuthLinkQuery(GOOGLE, REDIRECT_URI));

        // then
        assertAll(
                () -> verify(googleOAuthUriGenerator, times(1)).generate(REDIRECT_URI),
                () -> assertThat(uri).isEqualTo(googleOAuthUri)
        );
    }
}
