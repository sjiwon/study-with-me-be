package com.kgu.studywithme.global.config;

import com.kgu.studywithme.auth.utils.TokenProvider;
import com.kgu.studywithme.global.interceptor.RequestLogInterceptor;
import com.kgu.studywithme.global.interceptor.TokenValidityInterceptor;
import com.kgu.studywithme.global.resolver.ExtractPayloadArgumentResolver;
import com.kgu.studywithme.global.resolver.ExtractTokenArgumentResolver;
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
    private final TokenValidityInterceptor tokenValidityInterceptor;
    private final RequestLogInterceptor requestLogInterceptor;
    private final TokenProvider tokenProvider;

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(corsProperties.getAllowedOriginPatterns().toArray(String[]::new))
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(tokenValidityInterceptor);
        registry.addInterceptor(requestLogInterceptor)
                .addPathPatterns("/**")
                .order(1);
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new ExtractTokenArgumentResolver());
        resolvers.add(new ExtractPayloadArgumentResolver(tokenProvider));
    }
}
