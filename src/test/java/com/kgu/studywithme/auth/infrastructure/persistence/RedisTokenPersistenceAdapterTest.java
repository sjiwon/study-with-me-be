package com.kgu.studywithme.auth.infrastructure.persistence;

import com.kgu.studywithme.common.RedisTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static com.kgu.studywithme.auth.domain.RedisTokenKey.REFRESH_TOKEN_KEY;
import static com.kgu.studywithme.common.utils.TokenUtils.REFRESH_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Auth -> RedisTokenPersistenceAdapter 테스트")
public class RedisTokenPersistenceAdapterTest extends RedisTest {
    @Autowired
    private RedisTokenPersistenceAdapter redisTokenPersistenceAdapter;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private ValueOperations<String, String> operations;

    private static final Long MEMBER_ID = 1L;

    @BeforeEach
    void setUp() {
        operations = redisTemplate.opsForValue();
    }

    @Nested
    @DisplayName("RefreshToken 동기화")
    class SynchronizedRefreshToken {
        @Test
        @DisplayName("RefreshToken을 보유하고 있지 않은 사용자는 새로운 RefreshToken을 발급한다")
        void reissueRefreshToken() {
            // when
            redisTokenPersistenceAdapter.synchronizeRefreshToken(MEMBER_ID, REFRESH_TOKEN);

            // then
            final String token = operations.get(String.format(REFRESH_TOKEN_KEY.getValue(), MEMBER_ID));
            assertAll(
                    () -> assertThat(token).isNotNull(),
                    () -> assertThat(token).isEqualTo(REFRESH_TOKEN)
            );
        }

        @Test
        @DisplayName("RefreshToken을 보유하고 있는 사용자는 새로운 RefreshToken으로 업데이트한다")
        void updateRefreshToken() {
            // given
            operations.set(String.format(REFRESH_TOKEN_KEY.getValue(), MEMBER_ID), REFRESH_TOKEN);

            // when
            final String newRefreshToken = REFRESH_TOKEN + "new";
            redisTokenPersistenceAdapter.synchronizeRefreshToken(MEMBER_ID, newRefreshToken);

            // then
            final String token = redisTemplate.opsForValue().get(String.format(REFRESH_TOKEN_KEY.getValue(), MEMBER_ID));
            assertAll(
                    () -> assertThat(token).isNotNull(),
                    () -> assertThat(token).isEqualTo(newRefreshToken)
            );
        }
    }

    @Test
    @DisplayName("사용자의 RefreshToken을 재발급한다")
    void updateMemberRefreshToken() {
        // given
        operations.set(String.format(REFRESH_TOKEN_KEY.getValue(), MEMBER_ID), REFRESH_TOKEN);

        // when
        final String newRefreshToken = REFRESH_TOKEN + "new";
        redisTokenPersistenceAdapter.updateMemberRefreshToken(MEMBER_ID, newRefreshToken);

        // then
        final String token = redisTemplate.opsForValue().get(String.format(REFRESH_TOKEN_KEY.getValue(), MEMBER_ID));
        assertAll(
                () -> assertThat(token).isNotNull(),
                () -> assertThat(token).isEqualTo(newRefreshToken)
        );
    }

    @Test
    @DisplayName("사용자가 보유하고 있는 RefreshToken을 삭제한다")
    void deleteMemberRefreshToken() {
        // given
        operations.set(String.format(REFRESH_TOKEN_KEY.getValue(), MEMBER_ID), REFRESH_TOKEN);

        // when
        redisTokenPersistenceAdapter.deleteMemberRefreshToken(MEMBER_ID);

        // then
        final String token = redisTemplate.opsForValue().get(String.format(REFRESH_TOKEN_KEY.getValue(), MEMBER_ID));
        assertThat(token).isNull();
    }

    @Test
    @DisplayName("사용자가 보유하고 있는 RefreshToken인지 확인한다")
    void isMemberRefreshToken() {
        // given
        operations.set(String.format(REFRESH_TOKEN_KEY.getValue(), MEMBER_ID), REFRESH_TOKEN);

        // when
        final boolean actual1 = redisTokenPersistenceAdapter.isMemberRefreshToken(MEMBER_ID, REFRESH_TOKEN);
        final boolean actual2 = redisTokenPersistenceAdapter.isMemberRefreshToken(MEMBER_ID, REFRESH_TOKEN + "fake");

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}
