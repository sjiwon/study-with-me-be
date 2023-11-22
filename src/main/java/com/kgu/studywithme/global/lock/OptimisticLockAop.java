package com.kgu.studywithme.global.lock;

import com.kgu.studywithme.global.aop.AopWithTransactional;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OptimisticLockAop {
    private final AopWithTransactional aopWithTransactional;

    @Around("@annotation(com.kgu.studywithme.global.lock.OptimisticLockRetry)")
    public Object checkAuthUser(final ProceedingJoinPoint joinPoint) {
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        final Method method = signature.getMethod();
        final OptimisticLockRetry optimisticLockRetry = method.getAnnotation(OptimisticLockRetry.class);

        int currentRetry = 0;
        while (true) {
            try {
                if (tryMaximum(currentRetry++, optimisticLockRetry, method)) {
                    throw new RuntimeException("Retry Exception...");
                }

                if (optimisticLockRetry.withInTranction()) {
                    return aopWithTransactional.proceed(joinPoint);
                }
                return joinPoint.proceed();
            } catch (final OptimisticLockException e) {
                log.info(
                        "Optimistic Lock Version Miss... -> retry = {}, maxRetry = {}, withInTransaction = {}",
                        currentRetry,
                        optimisticLockRetry.maxRetry(),
                        optimisticLockRetry.withInTranction()
                );
                try {
                    Thread.sleep(50);
                } catch (final InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            } catch (final StudyWithMeException e) {
                throw e;
            } catch (final Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean tryMaximum(final int currentRetry, final OptimisticLockRetry optimisticLockRetry, final Method method) {
        if (optimisticLockRetry.maxRetry() != -1 && optimisticLockRetry.maxRetry() == currentRetry) {
            log.error("Retry Exception... -> method = {}", method);
            return true;
        }
        return false;
    }
}
