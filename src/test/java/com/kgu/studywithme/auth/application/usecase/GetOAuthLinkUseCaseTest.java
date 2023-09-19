package com.kgu.studywithme.auth.application.usecase;

import com.kgu.studywithme.auth.application.adapter.OAuthUriGenerator;
import com.kgu.studywithme.auth.application.usecase.query.GetOAuthLink;
import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.auth.infrastructure.oauth.google.GoogleOAuthProperties;
import com.kgu.studywithme.auth.infrastructure.oauth.google.GoogleOAuthUriGenerator;
import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Set;

import static com.kgu.studywithme.auth.domain.model.oauth.OAuthProvider.GOOGLE;
import static com.kgu.studywithme.common.utils.OAuthUtils.REDIRECT_URI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Auth -> GetOAuthLinkUseCase 테스트")
class GetOAuthLinkUseCaseTest extends UseCaseTest {
    private final GoogleOAuthProperties googleOAuthProperties = new GoogleOAuthProperties(
            "authorization_code",
            "client_id",
            "client_secret",
            "http://localhost:3000/login",
            Set.of("openid", "profile", "email"),
            "https://accounts.google.com/o/oauth2/v2/auth",
            "https://www.googleapis.com/oauth2/v4/token",
            "https://www.googleapis.com/oauth2/v3/userinfo"
    );
    private final GoogleOAuthUriGenerator googleOAuthUriGenerator = new GoogleOAuthUriGenerator(googleOAuthProperties);
    private final List<OAuthUriGenerator> oAuthUriGenerators = List.of(googleOAuthUriGenerator);
    private final GetOAuthLinkUseCase sut = new GetOAuthLinkUseCase(oAuthUriGenerators);

    @Test
    @DisplayName("제공하지 않는 OAuth Provider에 대해서는 예외가 발생한다")
    void throwExceptionByInvalidOAuthProvider() {
        assertThatThrownBy(() -> sut.invoke(new GetOAuthLink(null, REDIRECT_URI)))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(AuthErrorCode.INVALID_OAUTH_PROVIDER.getMessage());
    }

    @Test
    @DisplayName("Google Provider에 대해서 생성된 OAuthUri를 응답받는다")
    void success() {
        // when
        final String uri = sut.invoke(new GetOAuthLink(GOOGLE, REDIRECT_URI));

        // then
        final MultiValueMap<String, String> queryParams = UriComponentsBuilder
                .fromUriString(uri)
                .build()
                .getQueryParams();

        assertAll(
                () -> assertThat(queryParams.getFirst("response_type")).isEqualTo("code"),
                () -> assertThat(queryParams.getFirst("client_id")).isEqualTo(googleOAuthProperties.getClientId()),
                () -> assertThat(queryParams.getFirst("scope")).isEqualTo(String.join(" ", googleOAuthProperties.getScope())),
                () -> assertThat(queryParams.getFirst("redirect_uri")).isEqualTo(googleOAuthProperties.getRedirectUri()),
                () -> assertThat(queryParams.getFirst("state")).isNotNull()
        );
    }
}
