package com.kgu.studywithme.acceptance.study;

import com.kgu.studywithme.common.AcceptanceTest;
import com.kgu.studywithme.common.config.DatabaseCleanerEachCallbackExtension;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디를_생성한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디를_수정한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디를_종료시킨다;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.JPA;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@ExtendWith(DatabaseCleanerEachCallbackExtension.class)
@DisplayName("[Acceptance Test] 스터디 관련 기능")
public class StudyAcceptanceTest extends AcceptanceTest {
    private String hostAccessToken;

    @BeforeEach
    void setUp() {
        hostAccessToken = JIWON.회원가입_후_Google_OAuth_로그인을_진행하고_AccessToken을_추출한다();
    }

    @Nested
    @DisplayName("스터디 생성 API")
    class CreateStudyApi {
        @Test
        @DisplayName("스터디를 생성한다")
        void success() {
            스터디를_생성한다(hostAccessToken, SPRING)
                    .statusCode(CREATED.value())
                    .body("studyId", Matchers.notNullValue(Long.class));
        }
    }

    @Nested
    @DisplayName("스터디 수정 API")
    class UpdateStudyApi {
        @Test
        @DisplayName("스터디를 수정한다")
        void success() {
            final Long studyId = SPRING.스터디를_생성한다(hostAccessToken);

            스터디를_수정한다(hostAccessToken, studyId, JPA)
                    .statusCode(NO_CONTENT.value());
        }
    }

    @Nested
    @DisplayName("스터디 종료 API")
    class TerminateStudyApi {
        @Test
        @DisplayName("스터디를 종료시킨다")
        void success() {
            final Long studyId = SPRING.스터디를_생성한다(hostAccessToken);

            스터디를_종료시킨다(hostAccessToken, studyId)
                    .statusCode(NO_CONTENT.value());
        }
    }
}
