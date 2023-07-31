package com.kgu.studywithme.acceptance;

import com.kgu.studywithme.common.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.acceptance.fixture.MemberAcceptanceFixture.회원가입을_진행한다;
import static org.springframework.http.HttpStatus.CREATED;

@DisplayName("[Acceptance Test] 사용자 관련 기능")
public class MemberAcceptanceTest extends AcceptanceTest {
    @Test
    @DisplayName("회원가입을 진행한다")
    void signUpApi() {
        회원가입을_진행한다()
                .statusCode(CREATED.value());
    }
}
