package com.kgu.studywithme.common.config;

import com.kgu.studywithme.global.config.CorsProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.Set;

@TestConfiguration
public class TestWebBeanConfiguration {
    @Bean
    public CorsProperties corsProperties() {
        return new CorsProperties(Set.of("http://localhost:8080"));
    }
}
