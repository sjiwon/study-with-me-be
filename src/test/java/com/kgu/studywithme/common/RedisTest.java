package com.kgu.studywithme.common;

import com.kgu.studywithme.common.config.RedisTestContainersExtension;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;

@Tag("Redis")
@DataRedisTest
@ExtendWith(RedisTestContainersExtension.class)
public abstract class RedisTest {
}
