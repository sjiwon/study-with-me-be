package com.kgu.studywithme.auth.infrastructure.oauth.naver;

import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.auth.infrastructure.oauth.OAuthConnector;
import com.kgu.studywithme.auth.infrastructure.oauth.OAuthTokenResponse;
import com.kgu.studywithme.auth.infrastructure.oauth.OAuthUserResponse;
import com.kgu.studywithme.auth.infrastructure.oauth.naver.response.NaverTokenResponse;
import com.kgu.studywithme.auth.infrastructure.oauth.naver.response.NaverUserResponse;
import com.kgu.studywithme.auth.utils.OAuthProvider;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static com.kgu.studywithme.auth.utils.OAuthProvider.NAVER;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpMethod.GET;

@Component
@RequiredArgsConstructor
public class NaverOAuthConnector implements OAuthConnector {
    private static final String BEARER_TYPE = "Bearer";

    private final NaverOAuthProperties properties;
    private final RestTemplate restTemplate;

    @Override
    public boolean isSupported(final OAuthProvider provider) {
        return provider == NAVER;
    }

    @Override
    public OAuthTokenResponse getToken(
            final String code,
            final String redirectUri,
            final String state
    ) {
        final HttpHeaders headers = createTokenRequestHeader();
        final MultiValueMap<String, String> params = applyTokenRequestParams(code, state);

        final HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        return fetchNaverToken(request).getBody();
    }

    private HttpHeaders createTokenRequestHeader() {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }

    private MultiValueMap<String, String> applyTokenRequestParams(
            final String code,
            final String state
    ) {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", properties.getGrantType());
        params.add("code", code);
        params.add("state", state);
        params.add("client_id", properties.getClientId());
        params.add("client_secret", properties.getClientSecret());
        return params;
    }

    private ResponseEntity<NaverTokenResponse> fetchNaverToken(
            final HttpEntity<MultiValueMap<String, String>> request
    ) {
        try {
            return restTemplate.postForEntity(properties.getTokenUrl(), request, NaverTokenResponse.class);
        } catch (final RestClientException e) {
            throw StudyWithMeException.type(AuthErrorCode.GOOGLE_OAUTH_EXCEPTION);
        }
    }

    @Override
    public OAuthUserResponse getUserInfo(final String accessToken) {
        final HttpHeaders headers = createUserInfoRequestHeader(accessToken);
        final HttpEntity<Void> request = new HttpEntity<>(headers);
        return fetchNaverUserInfo(request).getBody();
    }

    private HttpHeaders createUserInfoRequestHeader(final String accessToken) {
        final HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, String.join(" ", BEARER_TYPE, accessToken));
        return headers;
    }

    private ResponseEntity<NaverUserResponse> fetchNaverUserInfo(final HttpEntity<Void> request) {
        try {
            return restTemplate.exchange(properties.getUserInfoUrl(), GET, request, NaverUserResponse.class);
        } catch (final RestClientException e) {
            throw StudyWithMeException.type(AuthErrorCode.GOOGLE_OAUTH_EXCEPTION);
        }
    }
}
