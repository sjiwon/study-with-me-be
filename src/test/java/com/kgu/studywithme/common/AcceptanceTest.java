package com.kgu.studywithme.common;

import com.kgu.studywithme.auth.infrastructure.oauth.google.GoogleOAuthConnector;
import com.kgu.studywithme.auth.infrastructure.oauth.google.GoogleOAuthUri;
import com.kgu.studywithme.common.config.ExternalApiConfiguration;
import com.kgu.studywithme.common.config.MySqlTestContainersConfiguration;
import com.kgu.studywithme.common.config.RedisTestContainersConfiguration;
import com.kgu.studywithme.common.utils.DatabaseCleaner;
import com.kgu.studywithme.upload.utils.S3FileUploader;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith({
        MySqlTestContainersConfiguration.class,
        RedisTestContainersConfiguration.class
})
@Import(ExternalApiConfiguration.class)
public abstract class AcceptanceTest {
    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @MockBean
    private GoogleOAuthUri googleOAuthUriMock;

    @MockBean
    private GoogleOAuthConnector googleOAuthConnectorMock;

    @MockBean
    private S3FileUploader s3FileUploaderMock;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @AfterEach
    void clear() {
        databaseCleaner.cleanUpDatabase();
    }
}