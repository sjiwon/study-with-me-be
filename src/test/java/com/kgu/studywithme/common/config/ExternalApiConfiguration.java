package com.kgu.studywithme.common.config;

import com.kgu.studywithme.auth.application.adapter.OAuthConnector;
import com.kgu.studywithme.auth.application.adapter.OAuthUriGenerator;
import com.kgu.studywithme.common.stub.StubEmailSender;
import com.kgu.studywithme.common.stub.StubFileUploader;
import com.kgu.studywithme.common.stub.StubOAuthConnector;
import com.kgu.studywithme.common.stub.StubOAuthUriGenerator;
import com.kgu.studywithme.file.application.adapter.FileUploader;
import com.kgu.studywithme.mail.application.adapter.EmailSender;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class ExternalApiConfiguration {
    @Bean
    public OAuthUriGenerator oAuthUri() {
        return new StubOAuthUriGenerator();
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
