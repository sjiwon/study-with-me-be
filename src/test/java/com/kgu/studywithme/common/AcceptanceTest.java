package com.kgu.studywithme.common;

import com.kgu.studywithme.common.annotation.MySqlTestContainers;
import com.kgu.studywithme.common.annotation.RedisTestContainers;
import com.kgu.studywithme.common.config.ExternalApiConfiguration;
import com.kgu.studywithme.common.utils.DatabaseCleaner;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(ExternalApiConfiguration.class)
@MySqlTestContainers
@RedisTestContainers
public abstract class AcceptanceTest {
    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        databaseCleaner.cleanUpDatabase();
    }
}
