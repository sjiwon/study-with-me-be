package com.kgu.studywithme.auth.domain;

import com.kgu.studywithme.common.ExecuteParallel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.common.utils.TokenUtils.REFRESH_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;

@ExecuteParallel
@DisplayName("Auth -> 도메인 [Token] 테스트")
class TokenTest {
    @Test
    @DisplayName("Token을 업데이트한다")
    void updateRefreshToken() {
        // given
        final Token token = Token.issueRefreshToken(1L, REFRESH_TOKEN);

        // when
        token.updateRefreshToken(REFRESH_TOKEN + "_update");

        // then
        assertThat(token.getRefreshToken()).isEqualTo(REFRESH_TOKEN + "_update");
    }
}
