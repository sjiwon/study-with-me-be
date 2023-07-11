package com.kgu.studywithme.auth.infrastructure.oauth.google;

import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.auth.infrastructure.oauth.google.response.GoogleTokenResponse;
import com.kgu.studywithme.auth.infrastructure.oauth.google.response.GoogleUserResponse;
import com.kgu.studywithme.common.utils.TokenUtils;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static com.kgu.studywithme.common.utils.TokenUtils.*;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@DisplayName("Auth [Infrastructure Layer] -> GoogleOAuthConnector 테스트")
class GoogleOAuthConnectorTest {
    @Autowired
    private GoogleOAuthConnector googleOAuthConnector;

    @Autowired
    private GoogleOAuthProperties properties;

    @MockBean
    private RestTemplate restTemplate;

    private static final String AUTHORIZATION_CODE = "authoriation_code";
    private static final String REDIRECT_URL = "http://localhost:8080/login/oauth2/code/google";

    @Nested
    @DisplayName("Token 응답받기")
    class getToken {
        @Test
        @DisplayName("Google Server와의 통신 불량으로 인해 예외가 발생한다")
        void failure() {
            // given
            given(restTemplate.postForEntity(eq(properties.getTokenUrl()), any(HttpEntity.class), eq(GoogleTokenResponse.class)))
                    .willThrow(RestClientException.class);

            // when - then
            assertThatThrownBy(() -> googleOAuthConnector.getToken(AUTHORIZATION_CODE, REDIRECT_URL))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(AuthErrorCode.GOOGLE_OAUTH_EXCEPTION.getMessage());
        }

        @Test
        @DisplayName("Authorization Code & RedirectUrl를 통해서 Google Authorization Server로부터 Token을 응답받는다")
        void success() {
            // given
            GoogleTokenResponse response = TokenUtils.createGoogleTokenResponse();
            ResponseEntity<GoogleTokenResponse> responseEntity = ResponseEntity.ok(response);
            given(restTemplate.postForEntity(eq(properties.getTokenUrl()), any(HttpEntity.class), eq(GoogleTokenResponse.class)))
                    .willReturn(responseEntity);

            // when
            GoogleTokenResponse result = (GoogleTokenResponse) googleOAuthConnector.getToken(AUTHORIZATION_CODE, REDIRECT_URL);

            // then
            assertAll(
                    () -> assertThat(result.tokenType()).isEqualTo(BEARER_TOKEN),
                    () -> assertThat(result.idToken()).isEqualTo(ID_TOKEN),
                    () -> assertThat(result.accessToken()).isEqualTo(ACCESS_TOKEN),
                    () -> assertThat(result.scope()).isEqualTo(SCOPE),
                    () -> assertThat(result.expiresIn()).isEqualTo(EXPIRES_IN)
            );
        }
    }

    @Nested
    @DisplayName("사용자 정보 응답받기")
    class getUserInfo {
        @Test
        @DisplayName("Google Server와의 통신 불량으로 인해 예외가 발생한다")
        void failure() {
            // given
            given(restTemplate.exchange(eq(properties.getUserInfoUrl()), eq(HttpMethod.GET), any(HttpEntity.class), eq(GoogleUserResponse.class)))
                    .willThrow(RestClientException.class);

            // when - then
            assertThatThrownBy(() -> googleOAuthConnector.getUserInfo(ACCESS_TOKEN))
                    .isInstanceOf(StudyWithMeException.class)
                    .hasMessage(AuthErrorCode.GOOGLE_OAUTH_EXCEPTION.getMessage());
        }

        @Test
        @DisplayName("Access Token을 통해서 Google Server에 저장된 사용자 정보를 응답받는다")
        void success() {
            // given
            GoogleUserResponse response = JIWON.toGoogleUserResponse();
            ResponseEntity<GoogleUserResponse> responseEntity = ResponseEntity.ok(response);
            given(restTemplate.exchange(eq(properties.getUserInfoUrl()), eq(HttpMethod.GET), any(HttpEntity.class), eq(GoogleUserResponse.class)))
                    .willReturn(responseEntity);

            // when
            GoogleUserResponse result = (GoogleUserResponse) googleOAuthConnector.getUserInfo(ACCESS_TOKEN);

            // then
            assertAll(
                    () -> assertThat(result.getName()).isEqualTo(JIWON.getName()),
                    () -> assertThat(result.getEmail()).isEqualTo(JIWON.getEmail()),
                    () -> assertThat(result.getProfileImage()).isEqualTo("google_profile_url")
            );
        }
    }
}
