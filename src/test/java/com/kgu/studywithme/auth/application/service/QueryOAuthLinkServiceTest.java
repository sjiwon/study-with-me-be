package com.kgu.studywithme.auth.application.service;

import com.kgu.studywithme.auth.application.usecase.query.QueryOAuthLinkUseCase;
import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.auth.infrastructure.oauth.OAuthUri;
import com.kgu.studywithme.auth.infrastructure.oauth.google.GoogleOAuthUri;
import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.util.ArrayList;
import java.util.List;

import static com.kgu.studywithme.auth.utils.OAuthProvider.GOOGLE;
import static com.kgu.studywithme.common.utils.OAuthUtils.REDIRECT_URI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Auth -> QueryOAuthLinkService 테스트")
class QueryOAuthLinkServiceTest extends UseCaseTest {
    @InjectMocks
    private QueryOAuthLinkService queryOAuthLinkService;

    @Spy
    private List<OAuthUri> oAuthUris = new ArrayList<>();

    @Mock
    private GoogleOAuthUri googleOAuthUri;

    @BeforeEach
    void setUp() {
        oAuthUris.add(googleOAuthUri);
    }

    @Test
    @DisplayName("제공하지 않는 OAuth Provider에 대해서는 예외가 발생한다")
    void throwExceptionByInvalidOAuthProvider() {
        // given
        given(googleOAuthUri.isSupported(any())).willReturn(false);

        // when - then
        assertThatThrownBy(
                () -> queryOAuthLinkService.queryOAuthLink(
                        new QueryOAuthLinkUseCase.Query(
                                null,
                                REDIRECT_URI
                        )
                )
        )
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(AuthErrorCode.INVALID_OAUTH_PROVIDER.getMessage());

        verify(googleOAuthUri, times(0)).generate(any());
    }

    @Test
    @DisplayName("Google Provider에 대해서 생성된 OAuthUri를 응답받는다")
    void successGoogle() {
        // given
        given(googleOAuthUri.isSupported(GOOGLE)).willReturn(true);
        given(googleOAuthUri.generate(any())).willReturn(REDIRECT_URI);

        // when
        final String uri = queryOAuthLinkService.queryOAuthLink(
                new QueryOAuthLinkUseCase.Query(
                        GOOGLE,
                        REDIRECT_URI
                )
        );

        // then
        assertAll(
                () -> verify(googleOAuthUri, times(1)).generate(any()),
                () -> assertThat(uri).isEqualTo(REDIRECT_URI)
        );
    }
}
