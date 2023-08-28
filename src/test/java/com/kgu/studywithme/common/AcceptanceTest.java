package com.kgu.studywithme.common;

import com.kgu.studywithme.auth.infrastructure.oauth.google.GoogleOAuthConnector;
import com.kgu.studywithme.auth.infrastructure.oauth.google.GoogleOAuthUriGenerator;
import com.kgu.studywithme.auth.infrastructure.oauth.kakao.KakaoOAuthConnector;
import com.kgu.studywithme.auth.infrastructure.oauth.kakao.KakaoOAuthUriGenerator;
import com.kgu.studywithme.auth.infrastructure.oauth.naver.NaverOAuthConnector;
import com.kgu.studywithme.auth.infrastructure.oauth.naver.NaverOAuthUriGenerator;
import com.kgu.studywithme.common.config.ExternalApiConfiguration;
import com.kgu.studywithme.common.config.MySqlTestContainersExtension;
import com.kgu.studywithme.common.config.RedisTestContainersExtension;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

@Tag("Acceptance")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith({
        MySqlTestContainersExtension.class,
        RedisTestContainersExtension.class
})
@Import(ExternalApiConfiguration.class)
public abstract class AcceptanceTest {
    @LocalServerPort
    private int port;

    @MockBean
    private GoogleOAuthUriGenerator googleOAuthUriMock;

    @MockBean
    private NaverOAuthUriGenerator naverOAuthUriMock;

    @MockBean
    private KakaoOAuthUriGenerator kakaoOAuthUriMock;

    @MockBean
    private GoogleOAuthConnector googleOAuthConnectorMock;

    @MockBean
    private NaverOAuthConnector naverOAuthConnectorMock;

    @MockBean
    private KakaoOAuthConnector kakaoOAuthConnectorMock;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }
}
