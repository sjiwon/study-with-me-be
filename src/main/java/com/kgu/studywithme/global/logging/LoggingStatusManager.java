package com.kgu.studywithme.global.logging;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class LoggingStatusManager {
    private final ThreadLocal<LoggingStatus> statusContainer = new ThreadLocal<>();

    public void syncStatus() {
        final LoggingStatus status = statusContainer.get();
        if (status == null) {
            final LoggingStatus firstLoggingStatus = createLoggingStatus();
            statusContainer.set(firstLoggingStatus);
        }
    }

    private LoggingStatus createLoggingStatus() {
        final String traceId = UUID.randomUUID().toString().substring(0, 8);
        return new LoggingStatus(traceId);
    }

    public LoggingStatus get() {
        return statusContainer.get();
    }

    public String getTaskId() {
        return statusContainer.get().getTaskId();
    }

    public void increaseDepth() {
        statusContainer.get().increaseDepth();
    }

    public void decreaseDepth() {
        statusContainer.get().decreaseDepth();
    }
}
