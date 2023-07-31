package com.kgu.studywithme.acceptance;

import com.kgu.studywithme.common.AcceptanceTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.acceptance.fixture.StudyAcceptanceFixture.스터디를_생성한다;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static org.springframework.http.HttpStatus.CREATED;

@DisplayName("[Acceptance Test] 스터디 관련 기능")
public class StudyAcceptanceTest extends AcceptanceTest {
    @Test
    @DisplayName("스터디를 생성한다")
    void createStudyApi() {
        final String hostAccessToken = JIWON.회원가입_후_Google_OAuth_로그인을_진행한다().accessToken();

        스터디를_생성한다(hostAccessToken, SPRING)
                .statusCode(CREATED.value())
                .body("studyId", Matchers.notNullValue(Long.class));
    }
}
