package com.kgu.studywithme.auth.utils;

import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.auth.utils.OAuthProvider.GOOGLE;
import static com.kgu.studywithme.auth.utils.OAuthProvider.KAKAO;
import static com.kgu.studywithme.auth.utils.OAuthProvider.NAVER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Auth -> OAuthProvider 테스트")
class OAuthProviderTest {
    @Test
    @DisplayName("제공하지 않는 Provider에 대해서 OAuthProvider를 가져오려고 하면 예외가 발생한다")
    void throwExceptionByInvalidOAuthProvider() {
        assertThatThrownBy(() -> OAuthProvider.from("anonymous"))
                .isInstanceOf(StudyWithMeException.class)
                .hasMessage(AuthErrorCode.INVALID_OAUTH_PROVIDER.getMessage());
    }

    @Test
    @DisplayName("주어진 Provider에 따른 OAuthProvider를 가져온다")
    void getSpecificOAuthProvider() {
        assertAll(
                () -> assertThat(OAuthProvider.from("google")).isEqualTo(GOOGLE),
                () -> assertThat(OAuthProvider.from("naver")).isEqualTo(NAVER),
                () -> assertThat(OAuthProvider.from("kakao")).isEqualTo(KAKAO)
        );
    }
}
