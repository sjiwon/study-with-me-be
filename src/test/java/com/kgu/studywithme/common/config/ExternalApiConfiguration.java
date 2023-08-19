package com.kgu.studywithme.common.config;

import com.kgu.studywithme.auth.application.adapter.OAuthConnector;
import com.kgu.studywithme.auth.application.adapter.OAuthUri;
import com.kgu.studywithme.common.stub.StubEmailSender;
import com.kgu.studywithme.common.stub.StubFileUploader;
import com.kgu.studywithme.common.stub.StubOAuthConnector;
import com.kgu.studywithme.common.stub.StubOAuthUri;
import com.kgu.studywithme.file.application.adapter.FileUploader;
import com.kgu.studywithme.mail.application.adapter.EmailSender;
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
