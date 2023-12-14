package com.kgu.studywithme.auth.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthorizationExtractor {
    private static final String BEARER_TYPE = "Bearer";

    public static Optional<String> extractAccessToken(final HttpServletRequest request) {
        final String token = request.getHeader(AUTHORIZATION);
        if (isEmptyToken(token)) {
            return Optional.empty();
        }
        return checkToken(token.split(" "));
    }

    public static Optional<String> extractRefreshToken(final HttpServletRequest request) {
        final String token = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(TokenResponseWriter.REFRESH_TOKEN_COOKIE))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        if (isEmptyToken(token)) {
            return Optional.empty();
        }
        return Optional.of(token);
    }

    private static boolean isEmptyToken(final String token) {
        return !StringUtils.hasText(token);
    }

    private static Optional<String> checkToken(final String[] parts) {
        if (parts.length == 2 && parts[0].equals(BEARER_TYPE)) {
            return Optional.ofNullable(parts[1]);
        }
        return Optional.empty();
    }
}
