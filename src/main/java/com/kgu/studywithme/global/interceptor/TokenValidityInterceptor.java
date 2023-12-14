package com.kgu.studywithme.global.interceptor;

import com.kgu.studywithme.auth.utils.AuthorizationExtractor;
import com.kgu.studywithme.auth.utils.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class TokenValidityInterceptor implements HandlerInterceptor {
    private final TokenProvider tokenProvider;

    @Override
    public boolean preHandle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler
    ) {
        if (CorsUtils.isPreFlightRequest(request)) {
            return true;
        }

        return AuthorizationExtractor.extractAccessToken(request)
                .map(tokenProvider::isTokenValid)
                .orElse(true);
    }
}
