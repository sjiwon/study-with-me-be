package com.kgu.studywithme.acceptance.member;

import com.kgu.studywithme.common.AcceptanceTest;
import com.kgu.studywithme.common.utils.DatabaseCleanerEachCallback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.kgu.studywithme.acceptance.member.MemberAcceptanceFixture.해당_사용자를_신고한다;
import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@ExtendWith(DatabaseCleanerEachCallback.class)
@DisplayName("[Acceptance Test] 사용자 신고 관련 기능")
public class MemberReportAcceptanceTest extends AcceptanceTest {
    private String reporterAccessToken;
    private Long reporteeId;

    @BeforeEach
    void setUp() {
        reporterAccessToken = JIWON.회원가입_후_Google_OAuth_로그인을_진행한다().token().accessToken();
        reporteeId = GHOST.회원가입을_진행한다();
    }

    @Nested
    @DisplayName("사용자 신고 API")
    class MemberReportApi {
        @Test
        @DisplayName("사용자를 신고한다")
        void success() {
            해당_사용자를_신고한다(reporterAccessToken, reporteeId)
                    .statusCode(NO_CONTENT.value());
        }
    }
}
