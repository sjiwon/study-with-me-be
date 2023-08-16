package com.kgu.studywithme.common;

import com.kgu.studywithme.auth.infrastructure.persistence.RedisTokenPersistenceAdapter;
import com.kgu.studywithme.common.config.RedisTestContainersConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;

@DataRedisTest
@ExtendWith(RedisTestContainersConfiguration.class)
@Import(RedisTokenPersistenceAdapter.class)
public abstract class RedisTest {
}
