package com.kgu.studywithme.acceptance.member;

import com.kgu.studywithme.common.AcceptanceTest;
import com.kgu.studywithme.common.utils.DatabaseCleanerEachCallback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.kgu.studywithme.acceptance.member.MemberAcceptanceFixture.작성한_리뷰를_수정한다;
import static com.kgu.studywithme.acceptance.member.MemberAcceptanceFixture.해당_사용자에게_리뷰를_작성한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디_신청자에_대한_참여를_승인한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디_주차를_생성한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디_참여_신청을_한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디를_생성하고_PK를_얻는다;
import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_1;
import static com.kgu.studywithme.memberreview.exception.MemberReviewErrorCode.COMMON_STUDY_RECORD_NOT_FOUND;
import static com.kgu.studywithme.memberreview.exception.MemberReviewErrorCode.MEMBER_REVIEW_NOT_FOUND;
import static com.kgu.studywithme.memberreview.exception.MemberReviewErrorCode.SELF_REVIEW_NOT_ALLOWED;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(DatabaseCleanerEachCallback.class)
@DisplayName("[Acceptance Test] 사용자 리뷰 관련 기능")
public class MemberReviewAcceptanceTest extends AcceptanceTest {
    private Long hostId;
    private String hostAccessToken;
    private Long memberId;
    private Long studyId;

    @BeforeEach
    void setUp() {
        hostId = JIWON.회원가입을_진행한다();
        hostAccessToken = JIWON.로그인을_진행한다().token().accessToken();
        memberId = GHOST.회원가입을_진행한다();
        final String memberAccessToken = GHOST.로그인을_진행한다().token().accessToken();

        studyId = 스터디를_생성하고_PK를_얻는다(hostAccessToken, SPRING);
        스터디_참여_신청을_한다(memberAccessToken, studyId);
    }

    @Nested
    @DisplayName("사용자 리뷰 작성 API")
    class writeReview {
        @Test
        @DisplayName("셀프 리뷰는 허용하지 않는다")
        void selfReviewNotAllowed() {
            해당_사용자에게_리뷰를_작성한다(hostAccessToken, hostId)
                    .statusCode(SELF_REVIEW_NOT_ALLOWED.getStatus().value())
                    .body("errorCode", is(SELF_REVIEW_NOT_ALLOWED.getErrorCode()))
                    .body("message", is(SELF_REVIEW_NOT_ALLOWED.getMessage()));
        }

        @Test
        @DisplayName("같이 스터디를 진행한 적이 없으면 리뷰를 남길 수 없다")
        void commonStudyRecordNotFound() {
            해당_사용자에게_리뷰를_작성한다(hostAccessToken, memberId)
                    .statusCode(COMMON_STUDY_RECORD_NOT_FOUND.getStatus().value())
                    .body("errorCode", is(COMMON_STUDY_RECORD_NOT_FOUND.getErrorCode()))
                    .body("message", is(COMMON_STUDY_RECORD_NOT_FOUND.getMessage()));
        }

        @Test
        @DisplayName("해당 사용자에게 리뷰를 작성한다")
        void success() {
            스터디_신청자에_대한_참여를_승인한다(hostAccessToken, studyId, memberId);
            스터디_주차를_생성한다(hostAccessToken, studyId, STUDY_WEEKLY_1);

            해당_사용자에게_리뷰를_작성한다(hostAccessToken, memberId)
                    .statusCode(OK.value());
        }
    }

    @Nested
    @DisplayName("사용자 리뷰 수정 API")
    class updateReview {
        @Test
        @DisplayName("작성한 리뷰가 없으면 수정할 수 없다")
        void failure() {
            작성한_리뷰를_수정한다(hostAccessToken, memberId)
                    .statusCode(MEMBER_REVIEW_NOT_FOUND.getStatus().value())
                    .body("errorCode", is(MEMBER_REVIEW_NOT_FOUND.getErrorCode()))
                    .body("message", is(MEMBER_REVIEW_NOT_FOUND.getMessage()));
        }

        @Test
        @DisplayName("작성한 리뷰를 수정한다")
        void success() {
            스터디_신청자에_대한_참여를_승인한다(hostAccessToken, studyId, memberId);
            스터디_주차를_생성한다(hostAccessToken, studyId, STUDY_WEEKLY_1);
            해당_사용자에게_리뷰를_작성한다(hostAccessToken, memberId);

            작성한_리뷰를_수정한다(hostAccessToken, memberId)
                    .statusCode(NO_CONTENT.value());
        }
    }
}
