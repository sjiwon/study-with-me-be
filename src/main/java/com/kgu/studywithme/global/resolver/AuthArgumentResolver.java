package com.kgu.studywithme.global.resolver;

import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.auth.utils.AuthorizationExtractor;
import com.kgu.studywithme.auth.utils.TokenProvider;
import com.kgu.studywithme.global.Authenticated;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
public class AuthArgumentResolver implements HandlerMethodArgumentResolver {
    private final TokenProvider tokenProvider;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Auth.class);
    }

    @Override
    public Object resolveArgument(
            final MethodParameter parameter,
            final ModelAndViewContainer mavContainer,
            final NativeWebRequest webRequest,
            final WebDataBinderFactory binderFactory
    ) {
        final HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        final String accessToken = AuthorizationExtractor.extractAccessToken(request)
                .orElseThrow(() -> StudyWithMeException.type(AuthErrorCode.INVALID_PERMISSION));
        final Long memberId = tokenProvider.getId(accessToken);
        return new Authenticated(memberId, accessToken);
    }
}