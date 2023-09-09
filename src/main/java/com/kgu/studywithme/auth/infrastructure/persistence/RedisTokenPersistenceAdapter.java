package com.kgu.studywithme.auth.infrastructure.persistence;

import com.kgu.studywithme.auth.application.adapter.TokenPersistenceAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import static com.kgu.studywithme.auth.domain.RedisTokenKey.REFRESH_TOKEN_KEY;
import static java.util.concurrent.TimeUnit.SECONDS;

@Primary
@Repository
public class RedisTokenPersistenceAdapter implements TokenPersistenceAdapter {
    private final long tokenValidityInMilliseconds;
    private final StringRedisTemplate tokenStorage;
    private final ValueOperations<String, String> tokenOperations;

    public RedisTokenPersistenceAdapter(
            @Value("${jwt.refresh-token-validity}") final long tokenValidityInMilliseconds,
            final StringRedisTemplate tokenStorage
    ) {
        this.tokenValidityInMilliseconds = tokenValidityInMilliseconds;
        this.tokenStorage = tokenStorage;
        tokenOperations = tokenStorage.opsForValue();
    }

    @Override
    public void synchronizeRefreshToken(final Long memberId, final String refreshToken) {
        tokenOperations.set(
                String.format(REFRESH_TOKEN_KEY.getValue(), memberId),
                refreshToken,
                tokenValidityInMilliseconds,
                SECONDS
        );
    }

    @Override
    public void updateMemberRefreshToken(final Long memberId, final String refreshToken) {
        tokenOperations.set(
                String.format(REFRESH_TOKEN_KEY.getValue(), memberId),
                refreshToken,
                tokenValidityInMilliseconds,
                SECONDS
        );
    }

    @Override
    public void deleteMemberRefreshToken(final Long memberId) {
        tokenStorage.delete(String.format(REFRESH_TOKEN_KEY.getValue(), memberId));
    }

    @Override
    public boolean isMemberRefreshToken(final Long memberId, final String refreshToken) {
        final String validToken = tokenOperations.get(String.format(REFRESH_TOKEN_KEY.getValue(), memberId));
        return refreshToken.equals(validToken);
    }
}
