package com.kgu.studywithme.global.resolver;

import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.auth.utils.AuthorizationExtractor;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class ExtractTokenArgumentResolver implements HandlerMethodArgumentResolver {
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

        if (extractToken.tokenType() == TokenType.ACCESS) {
            return AuthorizationExtractor.extractAccessToken(request)
                    .orElseThrow(() -> StudyWithMeException.type(AuthErrorCode.INVALID_PERMISSION));
        }
        return AuthorizationExtractor.extractRefreshToken(request)
                .orElseThrow(() -> StudyWithMeException.type(AuthErrorCode.INVALID_PERMISSION));
    }
}
