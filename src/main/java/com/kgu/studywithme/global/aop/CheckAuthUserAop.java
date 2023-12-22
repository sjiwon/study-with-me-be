package com.kgu.studywithme.global.aop;

import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.auth.utils.RequestTokenExtractor;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Aspect
@Component
public class CheckAuthUserAop {
    @Before("@annotation(com.kgu.studywithme.global.aop.CheckAuthUser)")
    public void checkAuthUser() {
        final HttpServletRequest request = getHttpServletRequest();
        final Optional<String> token = RequestTokenExtractor.extractAccessToken(request);

        if (token.isEmpty()) {
            throw StudyWithMeException.type(AuthErrorCode.INVALID_PERMISSION);
        }
    }

    private HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }
}
