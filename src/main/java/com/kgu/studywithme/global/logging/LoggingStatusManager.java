package com.kgu.studywithme.global.logging;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class LoggingStatusManager {
    private final ThreadLocal<LoggingStatus> statusContainer = new ThreadLocal<>();

    public void syncStatus() {
        final LoggingStatus status = statusContainer.get();
        if (status != null) {
            status.enterDepth();
            return;
        }
        final LoggingStatus firstLoggingStatus = createLoggingStatus();
        statusContainer.set(firstLoggingStatus);
    }

    private LoggingStatus createLoggingStatus() {
        final String traceId = UUID.randomUUID().toString().substring(0, 8);
        final long startTimeMillis = System.currentTimeMillis();
        return new LoggingStatus(traceId, startTimeMillis);
    }

    public String getTaskId() {
        final LoggingStatus status = getExistLoggingStatus();
        return status.getTaskId();
    }

    public long getStartTimeMillis() {
        final LoggingStatus status = getExistLoggingStatus();
        return status.getStartTimeMillis();
    }

    public int getDepthLevel() {
        final LoggingStatus status = getExistLoggingStatus();
        return status.getDepthLevel();
    }

    public void release() {
        final LoggingStatus status = getExistLoggingStatus();
        if (status.isEndDepth()) {
            statusContainer.remove();
            return;
        }
        status.leaveDepth();
    }

    private LoggingStatus getExistLoggingStatus() {
        final LoggingStatus status = statusContainer.get();
        if (status == null) {
            throw new IllegalStateException("Error During Logging Operation...");
        }
        return status;
    }
}
