package com.kgu.studywithme.common.config;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class RedisTestContainersConfiguration {
    private static final String REDIS_IMAGE = "redis:latest";
    private static final int REDIS_PORT = 6379;
    private static final GenericContainer<?> CONTAINER;

    static {
        CONTAINER = new GenericContainer(REDIS_IMAGE)
                .withExposedPorts(REDIS_PORT);
        CONTAINER.start();
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(final ConfigurableApplicationContext applicationContext) {
            TestPropertyValues.of(
                    "spring.data.redis.host=" + CONTAINER.getHost(),
                    "spring.data.redis.port=" + CONTAINER.getMappedPort(REDIS_PORT)
            ).applyTo(applicationContext.getEnvironment());
        }
    }
}
