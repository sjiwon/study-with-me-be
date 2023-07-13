package com.kgu.studywithme.auth.application.service;

import com.kgu.studywithme.auth.application.usecase.command.LogoutUseCase;
import com.kgu.studywithme.auth.domain.Token;
import com.kgu.studywithme.common.ServiceTest;
import com.kgu.studywithme.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static com.kgu.studywithme.common.utils.TokenUtils.REFRESH_TOKEN;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Auth -> LogoutService 테스트")
class LogoutServiceTest extends ServiceTest {
    @Autowired
    private LogoutService logoutService;

    @Test
    @DisplayName("로그아웃을 진행하면 사용자에게 발급되었던 RefreshToken이 Persistence Store(RDB / Redis / ...)에서 삭제된다")
    void logout() {
        // given
        final Member member = memberRepository.save(JIWON.toMember());
        tokenRepository.save(Token.issueRefreshToken(member.getId(), REFRESH_TOKEN));

        // when
        logoutService.logout(new LogoutUseCase.Command(member.getId()));

        // then
        Optional<Token> findToken = tokenRepository.findByMemberId(member.getId());
        assertThat(findToken).isEmpty();
    }
}
