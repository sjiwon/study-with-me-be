package com.kgu.studywithme.global.resolver;

import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.auth.utils.TokenProvider;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Optional;

import static com.kgu.studywithme.auth.utils.AuthorizationExtractor.extractAccessToken;
import static com.kgu.studywithme.auth.utils.AuthorizationExtractor.extractRefreshToken;

@RequiredArgsConstructor
public class ExtractTokenArgumentResolver implements HandlerMethodArgumentResolver {
    private final TokenProvider tokenProvider;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(ExtractToken.class);
    }

    @Override
    public Object resolveArgument(
            final MethodParameter parameter,
            final ModelAndViewContainer mavContainer,
            final NativeWebRequest webRequest,
            final WebDataBinderFactory binderFactory
    ) {
        final HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        final ExtractToken extractToken = parameter.getParameterAnnotation(ExtractToken.class);

        final String token = getToken(request, extractToken.tokenType())
                .orElseThrow(() -> StudyWithMeException.type(AuthErrorCode.INVALID_PERMISSION));
        tokenProvider.isTokenValid(token);
        return token;
    }

    private Optional<String> getToken(final HttpServletRequest request, final TokenType type) {
        return (type == TokenType.ACCESS) ? extractAccessToken(request) : extractRefreshToken(request);
    }
}
