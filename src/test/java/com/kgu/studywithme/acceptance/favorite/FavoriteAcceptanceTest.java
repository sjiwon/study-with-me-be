package com.kgu.studywithme.acceptance.favorite;

import com.kgu.studywithme.common.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.acceptance.favorite.FavoriteAcceptanceFixture.스터디를_찜_등록한다;
import static com.kgu.studywithme.acceptance.favorite.FavoriteAcceptanceFixture.찜_등록한_스터디를_취소한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디를_생성하고_PK를_얻는다;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@DisplayName("[Acceptance Test] 스터디 찜 관련 기능")
public class FavoriteAcceptanceTest extends AcceptanceTest {
    private String accessToken;
    private Long studyId;

    @BeforeEach
    void setUp() {
        accessToken = JIWON.회원가입_후_Google_OAuth_로그인을_진행한다().accessToken();
        studyId = 스터디를_생성하고_PK를_얻는다(accessToken, SPRING);
    }

    @Test
    @DisplayName("스터디를 찜 등록한다")
    void likeMarkingApi() {
        스터디를_찜_등록한다(accessToken, studyId)
                .statusCode(NO_CONTENT.value());
    }

    @Test
    @DisplayName("찜 등록되지 않은 스터디를 찜 취소할 수 없다")
    void likeCancellationApiFailure() {
        찜_등록한_스터디를_취소한다(accessToken, studyId)
                .statusCode(CONFLICT.value());
    }

    @Test
    @DisplayName("찜 등록한 스터디를 취소한다")
    void likeCancellationApiSuccess() {
        스터디를_찜_등록한다(accessToken, studyId);

        찜_등록한_스터디를_취소한다(accessToken, studyId)
                .statusCode(NO_CONTENT.value());
    }
}
