package com.kgu.studywithme.common.config;

import org.flywaydb.test.junit5.annotation.FlywayTestExtension;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@FlywayTestExtension
public class MySqlTestContainersConfiguration {
    private static final String MYSQL_IMAGE = "mysql:8.0.33";
    private static final String DATABASE_NAME = "study_with_me";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "1234";
    private static final MySQLContainer<?> CONTAINER;

    static {
        CONTAINER = new MySQLContainer(MYSQL_IMAGE)
                .withDatabaseName(DATABASE_NAME)
                .withUsername(USERNAME)
                .withPassword(PASSWORD);
        CONTAINER.start();
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(final ConfigurableApplicationContext applicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + CONTAINER.getJdbcUrl(),
                    "spring.datasource.username=" + CONTAINER.getUsername(),
                    "spring.datasource.password=" + CONTAINER.getPassword(),
                    "spring.flyway.url=" + CONTAINER.getJdbcUrl(),
                    "spring.flyway.user=" + CONTAINER.getUsername(),
                    "spring.flyway.password=" + CONTAINER.getPassword()
            ).applyTo(applicationContext.getEnvironment());
        }
    }
}
