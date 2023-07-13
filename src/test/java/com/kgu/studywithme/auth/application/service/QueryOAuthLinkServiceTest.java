package com.kgu.studywithme.auth.application.service;

import com.kgu.studywithme.auth.application.usecase.query.QueryOAuthLinkUseCase;
import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.auth.utils.OAuthProvider;
import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Auth -> QueryOAuthLinkService 테스트")
class QueryOAuthLinkServiceTest extends ServiceTest {
    @Autowired
    private QueryOAuthLinkService queryOAuthLinkService;

    @Test
    @DisplayName("제공하지 않는 OAuth Provider에 대해서는 예외가 발생한다")
    void throwExceptionByInvalidOAuthProvider() {
        assertThatThrownBy(
                () -> queryOAuthLinkService.createOAuthLink(
                        new QueryOAuthLinkUseCase.Query(
                                null,
                                "google-redirect-url"
                        )
                )
        )
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(AuthErrorCode.INVALID_OAUTH_PROVIDER.getMessage());
    }

    @Test
    @DisplayName("Google Provider에 대해서 생성된 OAuthUri를 응답받는다")
    void googleSuccess() {
        // when
        String uri = queryOAuthLinkService.createOAuthLink(
                new QueryOAuthLinkUseCase.Query(
                        OAuthProvider.GOOGLE,
                        "google-redirect-url"
                )
        );

        // then
        MultiValueMap<String, String> queryParams = UriComponentsBuilder
                .fromUriString(uri)
                .build()
                .getQueryParams();

        assertAll(
                () -> assertThat(queryParams.getFirst("response_type")).isEqualTo("code"),
                () -> assertThat(queryParams.getFirst("redirect_uri")).isEqualTo("google-redirect-url")
        );
    }
}
