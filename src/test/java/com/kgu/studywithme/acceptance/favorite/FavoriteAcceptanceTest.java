package com.kgu.studywithme.acceptance.favorite;

import com.kgu.studywithme.common.AcceptanceTest;
import com.kgu.studywithme.common.config.DatabaseCleanerEachCallbackExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.kgu.studywithme.acceptance.favorite.FavoriteAcceptanceFixture.스터디를_찜_등록한다;
import static com.kgu.studywithme.acceptance.favorite.FavoriteAcceptanceFixture.찜_등록한_스터디를_취소한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디를_생성하고_PK를_얻는다;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.favorite.exception.FavoriteErrorCode.NEVER_LIKE_MARKED;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@ExtendWith(DatabaseCleanerEachCallbackExtension.class)
@DisplayName("[Acceptance Test] 스터디 찜 관련 기능")
public class FavoriteAcceptanceTest extends AcceptanceTest {
    private String accessToken;
    private Long studyId;

    @BeforeEach
    void setUp() {
        accessToken = JIWON.회원가입_후_Google_OAuth_로그인을_진행한다().token().accessToken();
        studyId = 스터디를_생성하고_PK를_얻는다(accessToken, SPRING);
    }

    @Nested
    @DisplayName("스터디 찜 API")
    class LikeMarkingApi {
        @Test
        @DisplayName("스터디를 찜 등록한다")
        void success() {
            스터디를_찜_등록한다(accessToken, studyId)
                    .statusCode(NO_CONTENT.value());
        }
    }

    @Nested
    @DisplayName("스터디 찜 취소 API")
    class LikeCancellationApi {
        @Test
        @DisplayName("찜 등록되지 않은 스터디를 찜 취소할 수 없다")
        void failure() {
            찜_등록한_스터디를_취소한다(accessToken, studyId)
                    .statusCode(NEVER_LIKE_MARKED.getStatus().value())
                    .body("errorCode", is(NEVER_LIKE_MARKED.getErrorCode()))
                    .body("message", is(NEVER_LIKE_MARKED.getMessage()));
        }

        @Test
        @DisplayName("찜 등록한 스터디를 취소한다")
        void success() {
            스터디를_찜_등록한다(accessToken, studyId);

            찜_등록한_스터디를_취소한다(accessToken, studyId)
                    .statusCode(NO_CONTENT.value());
        }
    }
}
