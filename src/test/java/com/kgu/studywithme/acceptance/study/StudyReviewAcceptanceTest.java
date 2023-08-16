package com.kgu.studywithme.acceptance.study;

import com.kgu.studywithme.common.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디_리뷰를_작성한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디_신청자에_대한_참여를_승인한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디_참여_신청을_한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디를_졸업한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.작성한_스터디_리뷰를_삭제한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.작성한_스터디_리뷰를_수정한다;
import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.KAFKA;
import static com.kgu.studywithme.studyreview.exception.StudyReviewErrorCode.ONLY_GRADUATED_PARTICIPANT_CAN_WRITE_REVIEW;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@DisplayName("[Acceptance Test] 스터디 리뷰 관련 기능")
public class StudyReviewAcceptanceTest extends AcceptanceTest {
    private String participantAccessToken;
    private Long studyId;

    @BeforeEach
    void setUp() {
        final String hostAccessToken = JIWON.회원가입_후_Google_OAuth_로그인을_진행한다().token().accessToken();
        final Long participantId = GHOST.회원가입을_진행한다();
        participantAccessToken = GHOST.로그인을_진행한다().token().accessToken();
        studyId = KAFKA.스터디를_생성한다(hostAccessToken);
        스터디_참여_신청을_한다(participantAccessToken, studyId);
        스터디_신청자에_대한_참여를_승인한다(hostAccessToken, studyId, participantId);
    }

    @Nested
    @DisplayName("스터디 리뷰 작성 API")
    class WriteReviewApi {
        @Test
        @DisplayName("졸업하지 않은 참여자는 스터디 리뷰를 작성할 수 없다")
        void memberisNotGraduated() {
            스터디_리뷰를_작성한다(participantAccessToken, studyId)
                    .statusCode(ONLY_GRADUATED_PARTICIPANT_CAN_WRITE_REVIEW.getStatus().value())
                    .body("errorCode", is(ONLY_GRADUATED_PARTICIPANT_CAN_WRITE_REVIEW.getErrorCode()))
                    .body("message", is(ONLY_GRADUATED_PARTICIPANT_CAN_WRITE_REVIEW.getMessage()));
        }

        @Test
        @DisplayName("스터디 리뷰를 작성한다")
        void success() {
            스터디를_졸업한다(participantAccessToken, studyId);

            스터디_리뷰를_작성한다(participantAccessToken, studyId)
                    .statusCode(OK.value());
        }
    }

    @Nested
    @DisplayName("스터디 리뷰 수정 API")
    class UpdateReviewApi {
        private Long reviewId;

        @BeforeEach
        void setUp() {
            스터디를_졸업한다(participantAccessToken, studyId);
            reviewId = 스터디_리뷰를_작성한다(participantAccessToken, studyId)
                    .extract()
                    .jsonPath()
                    .getLong("reviewId");
        }

        @Test
        @DisplayName("스터디 리뷰를 수정한다")
        void success() {
            작성한_스터디_리뷰를_수정한다(participantAccessToken, studyId, reviewId)
                    .statusCode(NO_CONTENT.value());
        }
    }

    @Nested
    @DisplayName("스터디 리뷰 삭제 API")
    class DeleteReviewApi {
        private Long reviewId;

        @BeforeEach
        void setUp() {
            스터디를_졸업한다(participantAccessToken, studyId);
            reviewId = 스터디_리뷰를_작성한다(participantAccessToken, studyId)
                    .extract()
                    .jsonPath()
                    .getLong("reviewId");
        }

        @Test
        @DisplayName("스터디 리뷰를 삭제한다")
        void success() {
            작성한_스터디_리뷰를_삭제한다(participantAccessToken, studyId, reviewId)
                    .statusCode(NO_CONTENT.value());
        }
    }
}
