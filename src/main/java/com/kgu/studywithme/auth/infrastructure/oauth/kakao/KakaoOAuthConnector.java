package com.kgu.studywithme.auth.infrastructure.oauth.kakao;

import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.auth.infrastructure.oauth.OAuthConnector;
import com.kgu.studywithme.auth.infrastructure.oauth.OAuthTokenResponse;
import com.kgu.studywithme.auth.infrastructure.oauth.OAuthUserResponse;
import com.kgu.studywithme.auth.infrastructure.oauth.kakao.response.KakaoTokenResponse;
import com.kgu.studywithme.auth.infrastructure.oauth.kakao.response.KakaoUserResponse;
import com.kgu.studywithme.auth.utils.OAuthProvider;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static com.kgu.studywithme.auth.infrastructure.oauth.OAuthMetadata.CONTENT_TYPE_VALUE;
import static com.kgu.studywithme.auth.infrastructure.oauth.OAuthMetadata.TOKEN_TYPE;
import static com.kgu.studywithme.auth.utils.OAuthProvider.KAKAO;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.GET;

@Component
@RequiredArgsConstructor
public class KakaoOAuthConnector implements OAuthConnector {
    private final KakaoOAuthProperties properties;
    private final RestTemplate restTemplate;

    @Override
    public boolean isSupported(final OAuthProvider provider) {
        return provider == KAKAO;
    }

    @Override
    public OAuthTokenResponse getToken(
            final String code,
            final String redirectUri,
            final String state
    ) {
        final HttpHeaders headers = createTokenRequestHeader();
        final MultiValueMap<String, String> params = applyTokenRequestParams(code, redirectUri, state);

        final HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        return fetchKakaoToken(request).getBody();
    }

    private HttpHeaders createTokenRequestHeader() {
        final HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, CONTENT_TYPE_VALUE);
        return headers;
    }

    private MultiValueMap<String, String> applyTokenRequestParams(
            final String code,
            final String redirectUri,
            final String state
    ) {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", properties.getGrantType());
        params.add("code", code);
        params.add("redirect_uri", redirectUri);
        params.add("state", state);
        params.add("client_id", properties.getClientId());
        params.add("client_secret", properties.getClientSecret());
        return params;
    }

    private ResponseEntity<KakaoTokenResponse> fetchKakaoToken(
            final HttpEntity<MultiValueMap<String, String>> request
    ) {
        try {
            return restTemplate.postForEntity(properties.getTokenUrl(), request, KakaoTokenResponse.class);
        } catch (final RestClientException e) {
            throw StudyWithMeException.type(AuthErrorCode.GOOGLE_OAUTH_EXCEPTION);
        }
    }

    @Override
    public OAuthUserResponse getUserInfo(final String accessToken) {
        final HttpHeaders headers = createUserInfoRequestHeader(accessToken);
        final HttpEntity<Void> request = new HttpEntity<>(headers);
        return fetchKakaoUserInfo(request).getBody();
    }

    private HttpHeaders createUserInfoRequestHeader(final String accessToken) {
        final HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, CONTENT_TYPE_VALUE);
        headers.set(AUTHORIZATION, String.join(" ", TOKEN_TYPE, accessToken));
        return headers;
    }

    private ResponseEntity<KakaoUserResponse> fetchKakaoUserInfo(final HttpEntity<Void> request) {
        try {
            return restTemplate.exchange(properties.getUserInfoUrl(), GET, request, KakaoUserResponse.class);
        } catch (final RestClientException e) {
            throw StudyWithMeException.type(AuthErrorCode.GOOGLE_OAUTH_EXCEPTION);
        }
    }
}