package com.kgu.studywithme.common.config;

import com.kgu.studywithme.auth.infrastructure.oauth.OAuthConnector;
import com.kgu.studywithme.auth.infrastructure.oauth.OAuthUri;
import com.kgu.studywithme.common.stub.StubEmailSender;
import com.kgu.studywithme.common.stub.StubFileUploader;
import com.kgu.studywithme.common.stub.StubOAuthConnector;
import com.kgu.studywithme.common.stub.StubOAuthUri;
import com.kgu.studywithme.file.application.service.FileUploader;
import com.kgu.studywithme.mail.application.service.EmailSender;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

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
    @Primary
    public FileUploader fileUploader() {
        return new StubFileUploader();
    }

    @Bean
    @Primary
    public EmailSender emailSender() {
        return new StubEmailSender();
    }
}
