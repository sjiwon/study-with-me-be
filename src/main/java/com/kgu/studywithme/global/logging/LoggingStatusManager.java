package com.kgu.studywithme.global.logging;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class LoggingStatusManager {
    private LoggingStatus loggingStatus;

    public void applyLoggingStatus(final LoggingStatus loggingStatus) {
        this.loggingStatus = loggingStatus;
    }

    public LoggingStatus get() {
        return loggingStatus;
    }

    public void increaseDepth() {
        loggingStatus.increaseDepth();
    }

    public void decreaseDepth() {
        loggingStatus.decreaseDepth();
    }
}
