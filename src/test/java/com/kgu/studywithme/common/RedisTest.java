package com.kgu.studywithme.common;

import com.kgu.studywithme.common.config.RedisTestContainersConfiguration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;

@Tag("Redis")
@DataRedisTest
@ExtendWith(RedisTestContainersConfiguration.class)
public abstract class RedisTest {
}
