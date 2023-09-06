package com.kgu.studywithme.global.logging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoggingTracer {
    private static final String REQUEST_PREFIX = "--->";
    private static final String RESPONSE_PREFIX = "<---";
    private static final String EXCEPTION_PREFIX = "<X--";

    private final LoggingStatusManager loggingStatusManager;

    public void methodCall(
            final String methodSignature,
            final Object[] args
    ) {
        final LoggingStatus loggingStatus = loggingStatusManager.get();
        loggingStatusManager.increaseDepth();
        if (log.isInfoEnabled()) {
            log.info(
                    "[{}] {} args={}",
                    loggingStatus.getTaskId(),
                    loggingStatus.depthPrefix(REQUEST_PREFIX) + methodSignature,
                    args
            );
        }
    }

    public void methodReturn(final String methodSignature) {
        final LoggingStatus loggingStatus = loggingStatusManager.get();
        if (log.isInfoEnabled()) {
            log.info(
                    "[{}] {} time={}ms",
                    loggingStatus.getTaskId(),
                    loggingStatus.depthPrefix(RESPONSE_PREFIX) + methodSignature,
                    loggingStatus.totalTakenTime()
            );
        }
        loggingStatusManager.decreaseDepth();
    }

    public void throwException(final String methodSignature, final Throwable exception) {
        final LoggingStatus loggingStatus = loggingStatusManager.get();
        if (log.isInfoEnabled()) {
            log.info(
                    "[{}] {} time={}ms ex={}",
                    loggingStatus.getTaskId(),
                    loggingStatus.depthPrefix(EXCEPTION_PREFIX) + methodSignature,
                    loggingStatus.totalTakenTime(),
                    exception.toString()
            );
        }
        loggingStatusManager.decreaseDepth();
    }
}
