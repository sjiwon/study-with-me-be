package com.kgu.studywithme.auth.application.usecase;

import com.kgu.studywithme.auth.application.usecase.command.ReissueTokenCommand;
import com.kgu.studywithme.auth.domain.model.AuthToken;
import com.kgu.studywithme.auth.domain.service.TokenManager;
import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.common.UseCaseTest;
import com.kgu.studywithme.common.mock.fake.FakeTokenStore;
import com.kgu.studywithme.common.mock.stub.StubTokenProvider;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import com.kgu.studywithme.member.domain.model.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.kgu.studywithme.common.utils.TokenUtils.REFRESH_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Auth -> ReissueTokenUseCase 테스트")
class ReissueTokenUseCaseTest extends UseCaseTest {
    private final TokenManager tokenManager = new TokenManager(new StubTokenProvider(), new FakeTokenStore());
    private final ReissueTokenUseCase sut = new ReissueTokenUseCase(tokenManager);

    private final Member member = JIWON.toMember().apply(1L);

    @Test
    @DisplayName("사용자 소유의 RefreshToken이 아니면 재발급을 할 수 없다")
    void throwExceptionByInvalidRefreshToken() {
        assertThatThrownBy(() -> sut.invoke(new ReissueTokenCommand(member.getId(), REFRESH_TOKEN)))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(AuthErrorCode.INVALID_TOKEN.getMessage());
    }

    @Test
    @DisplayName("사용자 소유의 RefreshToken을 통해서 AccessToken과 RefreshToken을 재발급받는다")
    void success() {
        // given
        final AuthToken authToken = tokenManager.provideAuthorityToken(member.getId());

        // when
        final AuthToken response = sut.invoke(new ReissueTokenCommand(member.getId(), authToken.refreshToken()));

        // then
        assertAll(
                () -> assertThat(response.accessToken()).isEqualTo(ACCESS_TOKEN),
                () -> assertThat(response.refreshToken()).isEqualTo(REFRESH_TOKEN),
                () -> assertThat(tokenManager.isMemberRefreshToken(member.getId(), REFRESH_TOKEN)).isTrue()
        );
    }
}
