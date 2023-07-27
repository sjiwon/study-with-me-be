package com.kgu.studywithme.global.logging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoggingTracer {
    private static final String REQUEST_PREFIX = "-->";
    private static final String RESPONSE_PREFIX = "<--";
    private static final String EXCEPTION_PREFIX = "<X-";

    private final LoggingStatusManager loggingStatusManager;

    public void begin(
            final String methodSignature,
            final Object[] args
    ) {
        loggingStatusManager.syncStatus();
        if (log.isInfoEnabled()) {
            log.info(
                    "[{}] {}{} args={}",
                    loggingStatusManager.getTaskId(),
                    expressingDepth(REQUEST_PREFIX, loggingStatusManager.getDepthLevel()),
                    methodSignature,
                    args
            );
        }
    }

    public void end(final String methodSignature) {
        if (log.isInfoEnabled()) {
            final long stopTimeMillis = System.currentTimeMillis();
            final long resultTimeMillis = stopTimeMillis - loggingStatusManager.getStartTimeMillis();

            log.info(
                    "[{}] {} {} time={}ms",
                    loggingStatusManager.getTaskId(),
                    expressingDepth(RESPONSE_PREFIX, loggingStatusManager.getDepthLevel()),
                    methodSignature,
                    resultTimeMillis
            );
        }
        loggingStatusManager.release();
    }

    public void exception(
            final String methodSignature,
            final Exception e
    ) {
        if (log.isInfoEnabled()) {
            final long stopTimeMillis = System.currentTimeMillis();
            final long resultTimeMillis = stopTimeMillis - loggingStatusManager.getStartTimeMillis();

            log.info(
                    "[{}] {} {} time={}ms ex={}",
                    loggingStatusManager.getTaskId(),
                    expressingDepth(EXCEPTION_PREFIX, loggingStatusManager.getDepthLevel()),
                    methodSignature,
                    resultTimeMillis,
                    e.toString(),
                    e
            );
        }
        loggingStatusManager.release();
    }

    private static String expressingDepth(
            final String prefix,
            final int depthLevel
    ) {
        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < depthLevel; i++) {
            sb.append((i + 1 == depthLevel) ? "|" + prefix : "|\t");
        }

        return sb.toString();
    }
}
