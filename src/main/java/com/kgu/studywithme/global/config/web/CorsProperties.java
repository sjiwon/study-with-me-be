package com.kgu.studywithme.global.config.web;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

@Getter
@Component
public class CorsProperties {
    private final Set<String> allowedOriginPatterns;

    public CorsProperties(@Value("${cors.allowed-origin-patterns}") final Set<String> allowedOriginPatterns) {
        this.allowedOriginPatterns = allowedOriginPatterns;
    }
}
