package com.kgu.studywithme.auth.infrastructure.oauth.google;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@DisplayName("Auth -> GoogleOAuthUri 테스트")
class GoogleOAuthUriTest {
    @Autowired
    private GoogleOAuthUri googleOAuthUri;

    @Autowired
    private GoogleOAuthProperties properties;

    @Test
    @DisplayName("Google Authorization Server로부터 Access Token을 받기 위해서 선행적으로 Authorization Code를 요청하기 위한 URI를 생성한다")
    void generateAuthorizationCodeUri() {
        // when
        String uri = googleOAuthUri.generate(properties.getRedirectUrl());

        // then
        MultiValueMap<String, String> queryParams = UriComponentsBuilder
                .fromUriString(uri)
                .build()
                .getQueryParams();

        assertAll(
                () -> assertThat(queryParams.getFirst("response_type")).isEqualTo("code"),
                () -> assertThat(queryParams.getFirst("client_id")).isEqualTo(properties.getClientId()),
                () -> assertThat(queryParams.getFirst("scope")).isEqualTo(String.join(" ", properties.getScope())),
                () -> assertThat(queryParams.getFirst("redirect_uri")).isEqualTo(properties.getRedirectUrl())
        );
    }
}
