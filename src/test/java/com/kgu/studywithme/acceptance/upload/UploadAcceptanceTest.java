package com.kgu.studywithme.acceptance.upload;

import com.kgu.studywithme.common.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.acceptance.upload.UploadAcceptanceFixture.스터디_설명_내부_이미지를_업로드한다;
import static com.kgu.studywithme.acceptance.upload.UploadAcceptanceFixture.스터디_주차_글_내부_이미지를_업로드한다;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpStatus.OK;

@DisplayName("[Acceptance Test] 파일 업로드 관련 기능")
public class UploadAcceptanceTest extends AcceptanceTest {
    private String accessToken;

    @BeforeEach
    void setUp() {
        accessToken = JIWON.회원가입_후_Google_OAuth_로그인을_진행한다().accessToken();
    }

    @Nested
    @DisplayName("이미지 업로드 API")
    class ImageUploadApi {
        @Test
        @DisplayName("스터디 주차 글 내부 이미지를 업로드한다")
        void uploadStudyWeeklyImageApi() {
            스터디_주차_글_내부_이미지를_업로드한다(accessToken)
                    .statusCode(OK.value())
                    .body("result", is("https://weekly-image"));
        }

        @Test
        @DisplayName("스터디 설명 내부 이미지를 업로드한다")
        void uploadStudyDescriptionImageApi() {
            스터디_설명_내부_이미지를_업로드한다(accessToken)
                    .statusCode(OK.value())
                    .body("result", is("https://study-description-image"));
        }
    }
}
