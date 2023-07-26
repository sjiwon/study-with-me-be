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
    private static final MySQLContainer<?> CONTAINER;

    static {
        CONTAINER = new MySQLContainer(MYSQL_IMAGE)
                .withDatabaseName("study_with_me")
                .withUsername("root")
                .withPassword("1234");
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
