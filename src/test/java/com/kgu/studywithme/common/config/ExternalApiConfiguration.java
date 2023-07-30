package com.kgu.studywithme.common.config;

import com.kgu.studywithme.auth.infrastructure.oauth.OAuthConnector;
import com.kgu.studywithme.auth.infrastructure.oauth.OAuthUri;
import com.kgu.studywithme.common.stub.StubFileUploader;
import com.kgu.studywithme.common.stub.StubOAuthConnector;
import com.kgu.studywithme.common.stub.StubOAuthUri;
import com.kgu.studywithme.upload.utils.FileUploader;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class ExternalApiConfiguration {
    @Bean
    public OAuthUri oAuthUri() {
        return new StubOAuthUri();
    }

    @Bean
    public OAuthConnector oAuthConnector() {
        return new StubOAuthConnector();
    }

    @Bean
    public FileUploader fileUploader() {
        return new StubFileUploader();
    }
}
