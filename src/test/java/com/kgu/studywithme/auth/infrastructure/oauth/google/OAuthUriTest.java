package com.kgu.studywithme.auth.infrastructure.oauth.google;

import com.kgu.studywithme.auth.infrastructure.oauth.kakao.KakaoOAuthProperties;
import com.kgu.studywithme.auth.infrastructure.oauth.kakao.KakaoOAuthUri;
import com.kgu.studywithme.auth.infrastructure.oauth.naver.NaverOAuthProperties;
import com.kgu.studywithme.auth.infrastructure.oauth.naver.NaverOAuthUri;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(classes = {
        GoogleOAuthUri.class, GoogleOAuthProperties.class,
        NaverOAuthUri.class, NaverOAuthProperties.class,
        KakaoOAuthUri.class, KakaoOAuthProperties.class
})
@DisplayName("Auth -> OAuthUri [구글, 네이버, 카카오] 테스트")
class OAuthUriTest {
    @Autowired
    private GoogleOAuthUri googleOAuthUri;

    @Autowired
    private GoogleOAuthProperties googleOAuthProperties;

    @Autowired
    private NaverOAuthUri naverOAuthUri;

    @Autowired
    private NaverOAuthProperties naverOAuthProperties;

    @Autowired
    private KakaoOAuthUri kakaoOAuthUri;

    @Autowired
    private KakaoOAuthProperties kakaoOAuthProperties;

    @Test
    @DisplayName("Google Authorization Server로부터 Access Token을 받기 위해서 선행적으로 Authorization Code를 요청하기 위한 URI를 생성한다")
    void googleOAuthUri() {
        // when
        final String uri = googleOAuthUri.generate(googleOAuthProperties.getRedirectUri());

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

    @Test
    @DisplayName("Naver Authorization Server로부터 Access Token을 받기 위해서 선행적으로 Authorization Code를 요청하기 위한 URI를 생성한다")
    void naverOAuthUri() {
        // when
        final String uri = naverOAuthUri.generate(naverOAuthProperties.getRedirectUri());

        // then
        final MultiValueMap<String, String> queryParams = UriComponentsBuilder
                .fromUriString(uri)
                .build()
                .getQueryParams();

        assertAll(
                () -> assertThat(queryParams.getFirst("response_type")).isEqualTo("code"),
                () -> assertThat(queryParams.getFirst("client_id")).isEqualTo(naverOAuthProperties.getClientId()),
                () -> assertThat(queryParams.getFirst("redirect_uri")).isEqualTo(naverOAuthProperties.getRedirectUri()),
                () -> assertThat(queryParams.getFirst("state")).isNotNull()
        );
    }

    @Test
    @DisplayName("Kakao Authorization Server로부터 Access Token을 받기 위해서 선행적으로 Authorization Code를 요청하기 위한 URI를 생성한다")
    void kakaoOAuthUri() {
        // when
        final String uri = kakaoOAuthUri.generate(kakaoOAuthProperties.getRedirectUri());

        // then
        final MultiValueMap<String, String> queryParams = UriComponentsBuilder
                .fromUriString(uri)
                .build()
                .getQueryParams();

        assertAll(
                () -> assertThat(queryParams.getFirst("response_type")).isEqualTo("code"),
                () -> assertThat(queryParams.getFirst("client_id")).isEqualTo(kakaoOAuthProperties.getClientId()),
                () -> assertThat(queryParams.getFirst("scope")).isEqualTo(String.join(" ", kakaoOAuthProperties.getScope())),
                () -> assertThat(queryParams.getFirst("redirect_uri")).isEqualTo(kakaoOAuthProperties.getRedirectUri()),
                () -> assertThat(queryParams.getFirst("state")).isNotNull()
        );
    }
}
