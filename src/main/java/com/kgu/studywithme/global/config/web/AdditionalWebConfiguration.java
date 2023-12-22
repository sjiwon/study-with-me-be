package com.kgu.studywithme.global.config.web;

import com.kgu.studywithme.auth.utils.TokenProvider;
import com.kgu.studywithme.global.annotation.AuthArgumentResolver;
import com.kgu.studywithme.global.annotation.ExtractTokenArgumentResolver;
import com.kgu.studywithme.global.interceptor.RequestLogInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AdditionalWebConfiguration implements WebMvcConfigurer {
    private final CorsProperties corsProperties;
    private final RequestLogInterceptor requestLogInterceptor;
    private final TokenProvider tokenProvider;

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(corsProperties.getAllowedOriginPatterns().toArray(String[]::new))
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(requestLogInterceptor)
                .addPathPatterns("/**")
                .order(1);
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new AuthArgumentResolver(tokenProvider));
        resolvers.add(new ExtractTokenArgumentResolver(tokenProvider));
    }
}
