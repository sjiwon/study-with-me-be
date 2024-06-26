package com.kgu.studywithme.global.log;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {
    private final LoggingTracer loggingTracer;

    @Pointcut("execution(public * com.kgu.studywithme..*(..))")
    private void includeComponent() {
    }

    @Pointcut("""
                !execution(* com.kgu.studywithme.global.annotation..*(..))
                && !execution(* com.kgu.studywithme.global.aop..*(..))
                && !execution(* com.kgu.studywithme.global.config..*(..))
                && !execution(* com.kgu.studywithme.global.decorator..*(..))
                && !execution(* com.kgu.studywithme.global.filter..*(..))
                && !execution(* com.kgu.studywithme.global.log..*(..))
                && !execution(* com.kgu.studywithme..*Config.*(..))
                && !execution(* com.kgu.studywithme..*Formatter.*(..))
                && !execution(* com.kgu.studywithme..*Properties.*(..))
                && !execution(* com.kgu.studywithme..*TokenProvider.*(..))
                && !execution(* com.kgu.studywithme..*TokenResponseWriter.*(..))
                && !execution(* com.kgu.studywithme..*TokenExtractor.*(..))
            """)
    private void excludeComponent() {
    }

    @Around("includeComponent() && excludeComponent()")
    public Object doLogging(final ProceedingJoinPoint joinPoint) throws Throwable {
        final String methodSignature = joinPoint.getSignature().toShortString();
        final Object[] args = joinPoint.getArgs();
        loggingTracer.methodCall(methodSignature, args);
        try {
            final Object result = joinPoint.proceed();
            loggingTracer.methodReturn(methodSignature);
            return result;
        } catch (final Throwable e) {
            loggingTracer.throwException(methodSignature, e);
            throw e;
        }
    }
}
