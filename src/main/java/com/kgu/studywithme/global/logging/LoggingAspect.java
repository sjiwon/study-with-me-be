package com.kgu.studywithme.global.logging;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {
    private final LoggingStatusManager loggingStatusManager;
    private final LoggingTracer loggingTracer;

    @Pointcut("execution(public * com.kgu.studywithme..*(..)) && !execution(public * com.kgu.studywithme.global.logging..*(..))")
    private void allComponents() {
    }

    @Pointcut("execution(public * com.kgu.studywithme..*ApiController.*(..))")
    private void allController() {
    }

    @Around("allComponents()")
    public Object trackingDepth(final ProceedingJoinPoint joinPoint) throws Throwable {
        final String methodSignature = joinPoint.getSignature().toShortString();
        final Object[] args = joinPoint.getArgs();
        try {
            loggingTracer.begin(methodSignature, args);

            final Object result = joinPoint.proceed();

            loggingTracer.end(methodSignature);
            return result;
        } catch (final Exception e) {
            loggingTracer.exception(methodSignature, e);
            throw e;
        }
    }

    @Around("allController()")
    public Object trackingRequest(final ProceedingJoinPoint joinPoint) throws Throwable {
        loggingStatusManager.syncStatus();
        final String taskId = loggingStatusManager.getTaskId();

        final HttpServletRequest request = getServletRequest();
        final String method = request.getMethod();
        final String requestURI = request.getRequestURI();

        final Object[] args = joinPoint.getArgs();
        log.info("[{}] {} {} args={}", taskId, method, requestURI, args);

        try {
            return joinPoint.proceed();
        } finally {
            loggingStatusManager.release();
        }
    }

    private HttpServletRequest getServletRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

    @AfterReturning(
            value = "execution(public * com.kgu.studywithme..*ApiController.*(..))",
            returning = "result"
    )
    public void trackingResponse(
            final JoinPoint joinPoint,
            final Object result
    ) {
        final String taskId = loggingStatusManager.getTaskId();

        final MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        final String controllerMethodName = methodSignature.getMethod().getName();

        log.info("[{}] method: {}, result: {}", taskId, controllerMethodName, result);
    }
}
