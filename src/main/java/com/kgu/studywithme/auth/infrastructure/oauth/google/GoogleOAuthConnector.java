package com.kgu.studywithme.auth.infrastructure.oauth.google;

import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.auth.infrastructure.oauth.OAuthConnector;
import com.kgu.studywithme.auth.infrastructure.oauth.OAuthTokenResponse;
import com.kgu.studywithme.auth.infrastructure.oauth.OAuthUserResponse;
import com.kgu.studywithme.auth.infrastructure.oauth.google.response.GoogleTokenResponse;
import com.kgu.studywithme.auth.infrastructure.oauth.google.response.GoogleUserResponse;
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

import static com.kgu.studywithme.auth.utils.OAuthProvider.GOOGLE;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpMethod.GET;

@Component
@RequiredArgsConstructor
public class GoogleOAuthConnector implements OAuthConnector {
    private static final String BEARER_TYPE = "Bearer";

    private final GoogleOAuthProperties properties;
    private final RestTemplate restTemplate;

    @Override
    public boolean isSupported(final OAuthProvider provider) {
        return provider == GOOGLE;
    }

    @Override
    public OAuthTokenResponse getToken(
            final String code,
            final String redirectUri
    ) {
        final HttpHeaders headers = createTokenRequestHeader();
        final MultiValueMap<String, String> params = applyTokenRequestParams(code, redirectUri);

        final HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        return fetchGoogleToken(request).getBody();
    }

    private HttpHeaders createTokenRequestHeader() {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }

    private MultiValueMap<String, String> applyTokenRequestParams(
            final String code,
            final String redirectUri
    ) {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", properties.getGrantType());
        params.add("client_id", properties.getClientId());
        params.add("client_secret", properties.getClientSecret());
        params.add("code", code);
        params.add("redirect_uri", redirectUri);
        return params;
    }

    private ResponseEntity<GoogleTokenResponse> fetchGoogleToken(
            final HttpEntity<MultiValueMap<String, String>> request
    ) {
        try {
            return restTemplate.postForEntity(properties.getTokenUrl(), request, GoogleTokenResponse.class);
        } catch (final RestClientException e) {
            throw StudyWithMeException.type(AuthErrorCode.GOOGLE_OAUTH_EXCEPTION);
        }
    }

    @Override
    public OAuthUserResponse getUserInfo(final String accessToken) {
        final HttpHeaders headers = createUserInfoRequestHeader(accessToken);
        final HttpEntity<Void> request = new HttpEntity<>(headers);
        return fetchGoogleUserInfo(request).getBody();
    }

    private HttpHeaders createUserInfoRequestHeader(final String accessToken) {
        final HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, String.join(" ", BEARER_TYPE, accessToken));
        return headers;
    }

    private ResponseEntity<GoogleUserResponse> fetchGoogleUserInfo(final HttpEntity<Void> request) {
        try {
            return restTemplate.exchange(properties.getUserInfoUrl(), GET, request, GoogleUserResponse.class);
        } catch (final RestClientException e) {
            throw StudyWithMeException.type(AuthErrorCode.GOOGLE_OAUTH_EXCEPTION);
        }
    }
}
