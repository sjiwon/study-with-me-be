package com.kgu.studywithme.global.interceptor;

import com.kgu.studywithme.auth.utils.AuthorizationExtractor;
import com.kgu.studywithme.auth.utils.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

@RequiredArgsConstructor
public class TokenValidityInterceptor implements HandlerInterceptor {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean preHandle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler
    ) {
        if (CorsUtils.isPreFlightRequest(request)) {
            return true;
        }

        final Optional<String> token = AuthorizationExtractor.extractToken(request);
        return token
                .map(jwtTokenProvider::isTokenValid)
                .orElse(true);
    }
}
