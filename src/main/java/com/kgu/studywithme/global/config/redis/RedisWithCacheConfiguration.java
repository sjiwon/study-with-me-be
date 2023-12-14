package com.kgu.studywithme.global.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
@RequiredArgsConstructor
public class RedisWithCacheConfiguration {
    private final ObjectMapper objectMapper;

    @Bean
    @Primary
    public CacheManager cacheManager(final RedisConnectionFactory redisConnectionFactory) {
        return generateCacheManager(redisConnectionFactory, Duration.ofMinutes(1));
    }

    @Bean
    public CacheManager studyCacheManager(final RedisConnectionFactory redisConnectionFactory) {
        return generateCacheManager(redisConnectionFactory, Duration.ofMinutes(10));
    }

    @Bean
    public CacheManager memberInfoCacheManager(final RedisConnectionFactory redisConnectionFactory) {
        return generateCacheManager(redisConnectionFactory, Duration.ofDays(1));
    }

    private CacheManager generateCacheManager(final RedisConnectionFactory redisConnectionFactory, final Duration ttl) {
        final RedisCacheConfiguration redisCacheConfiguration = generateCacheConfiguration()
                .entryTtl(ttl);

        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .build();
    }

    private RedisCacheConfiguration generateCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .computePrefixWith(CacheKeyPrefix.simple())
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper)));
    }
}
