package com.kgu.studywithme.acceptance.member;

import com.kgu.studywithme.common.AcceptanceTest;
import com.kgu.studywithme.common.config.DatabaseCleanerEachCallbackExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.kgu.studywithme.acceptance.member.MemberAcceptanceFixture.사용자_정보를_수정한다;
import static com.kgu.studywithme.acceptance.member.MemberAcceptanceFixture.회원가입을_진행한다;
import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@ExtendWith(DatabaseCleanerEachCallbackExtension.class)
@DisplayName("[Acceptance Test] 사용자 관련 기능")
public class MemberAcceptanceTest extends AcceptanceTest {
    @Nested
    @DisplayName("회원가입 API")
    class SignUpApi {
        @Test
        @DisplayName("회원가입을 진행한다")
        void signUpApi() {
            회원가입을_진행한다(JIWON)
                    .statusCode(CREATED.value());
        }
    }

    @Nested
    @DisplayName("사용자 정보 수정 API")
    class UpdateApi {
        @Test
        @DisplayName("사용자 정보를 수정한다")
        void signUpApi() {
            final String accessToken = JIWON.회원가입_후_Google_OAuth_로그인을_진행하고_AccessToken을_추출한다();
            사용자_정보를_수정한다(accessToken, GHOST)
                    .statusCode(NO_CONTENT.value());
        }
    }
}
