package com.kgu.studywithme.auth.application.service;

import com.kgu.studywithme.auth.application.usecase.command.LogoutUseCase;
import com.kgu.studywithme.auth.infrastructure.token.TokenPersistenceAdapter;
import com.kgu.studywithme.common.UseCaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Auth -> LogoutService 테스트")
class LogoutServiceTest extends UseCaseTest {
    @InjectMocks
    private LogoutService logoutService;

    @Mock
    private TokenPersistenceAdapter tokenPersistenceAdapter;

    @Test
    @DisplayName("로그아웃을 진행하면 사용자에게 발급되었던 RefreshToken이 Persistence Store(RDB / Redis / ...)에서 삭제된다")
    void logout() {
        // when
        logoutService.logout(new LogoutUseCase.Command(1L));

        // then
        verify(tokenPersistenceAdapter, times(1)).deleteRefreshTokenByMemberId(any());
    }
}
