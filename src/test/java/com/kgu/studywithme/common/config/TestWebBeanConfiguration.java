package com.kgu.studywithme.common.config;

import com.kgu.studywithme.global.config.CorsProperties;
import com.kgu.studywithme.global.logging.LoggingStatus;
import com.kgu.studywithme.global.logging.LoggingStatusManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.Set;
import java.util.UUID;

@TestConfiguration
public class TestWebBeanConfiguration {
    @Bean
    public LoggingStatusManager loggingStatusManager() {
        final LoggingStatusManager loggingStatusManager = new LoggingStatusManager();

        final String taskId = UUID.randomUUID().toString().substring(0, 8);
        loggingStatusManager.applyLoggingStatus(new LoggingStatus(taskId));

        return loggingStatusManager;
    }

    @Bean
    public CorsProperties corsProperties() {
        return new CorsProperties(Set.of("http://localhost:8080"));
    }
}
