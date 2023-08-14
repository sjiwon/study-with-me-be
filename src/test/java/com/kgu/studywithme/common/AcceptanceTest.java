package com.kgu.studywithme.common;

import com.kgu.studywithme.auth.infrastructure.oauth.google.GoogleOAuthConnector;
import com.kgu.studywithme.auth.infrastructure.oauth.google.GoogleOAuthUri;
import com.kgu.studywithme.auth.infrastructure.oauth.kakao.KakaoOAuthConnector;
import com.kgu.studywithme.auth.infrastructure.oauth.kakao.KakaoOAuthUri;
import com.kgu.studywithme.auth.infrastructure.oauth.naver.NaverOAuthConnector;
import com.kgu.studywithme.auth.infrastructure.oauth.naver.NaverOAuthUri;
import com.kgu.studywithme.common.config.ExternalApiConfiguration;
import com.kgu.studywithme.common.config.MySqlTestContainersConfiguration;
import com.kgu.studywithme.common.config.RedisTestContainersConfiguration;
import com.kgu.studywithme.common.utils.DatabaseCleaner;
import com.kgu.studywithme.file.application.service.S3FileUploader;
import com.kgu.studywithme.mail.application.service.AwsSESEmailSender;
import com.kgu.studywithme.mail.application.service.DefaultEmailSender;
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
    private NaverOAuthUri naverOAuthUriMock;

    @MockBean
    private KakaoOAuthUri kakaoOAuthUriMock;

    @MockBean
    private GoogleOAuthConnector googleOAuthConnectorMock;

    @MockBean
    private NaverOAuthConnector naverOAuthConnectorMock;

    @MockBean
    private KakaoOAuthConnector kakaoOAuthConnectorMock;

    @MockBean
    private S3FileUploader s3FileUploaderMock;

    @MockBean
    private DefaultEmailSender defaultEmailSenderMock;

    @MockBean
    private AwsSESEmailSender awsSESEmailSenderMock;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @AfterEach
    void clear() {
        databaseCleaner.cleanUpDatabase();
    }
}
