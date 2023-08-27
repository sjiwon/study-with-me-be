package com.kgu.studywithme.acceptance.study;

import com.kgu.studywithme.common.AcceptanceTest;
import com.kgu.studywithme.common.utils.DatabaseCleanerEachCallback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디_공지사항에_댓글을_작성한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디_공지사항에_작성한_댓글을_삭제한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디_공지사항에_작성한_댓글을_수정한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디_공지사항을_작성한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디_공지사항을_조회한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디_신청자에_대한_참여를_승인한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디_참여_신청을_한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.작성한_스터디_공지사항을_삭제한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.작성한_스터디_공지사항을_수정한다;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY9;
import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.study.exception.StudyErrorCode.MEMBER_IS_NOT_HOST;
import static com.kgu.studywithme.studynotice.exception.StudyNoticeErrorCode.ONLY_PARTICIPANT_CAN_WRITE_COMMENT;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(DatabaseCleanerEachCallback.class)
@DisplayName("[Acceptance Test] 스터디 공지사항 관련 기능")
public class StudyNoticeAcceptanceTest extends AcceptanceTest {
    private String hostAccessToken;
    private String memberAccessToken;
    private Long studyId;

    @BeforeEach
    void setUp() {
        hostAccessToken = JIWON.회원가입_후_Google_OAuth_로그인을_진행한다().token().accessToken();
        final Long memberId = GHOST.회원가입을_진행한다();
        memberAccessToken = GHOST.로그인을_진행한다().token().accessToken();

        studyId = SPRING.스터디를_생성한다(hostAccessToken);
        스터디_참여_신청을_한다(memberAccessToken, studyId);
        스터디_신청자에_대한_참여를_승인한다(hostAccessToken, studyId, memberId);
    }

    @Nested
    @DisplayName("스터디 공지사항 API")
    class Notice {
        @Nested
        @DisplayName("스터디 공지사항 작성 API")
        class WriteNotice {
            @Test
            @DisplayName("팀장이 아니면 공지사항을 작성할 수 없다")
            void memberIsNotHost() {
                스터디_공지사항을_작성한다(memberAccessToken, studyId)
                        .statusCode(MEMBER_IS_NOT_HOST.getStatus().value())
                        .body("errorCode", is(MEMBER_IS_NOT_HOST.getErrorCode()))
                        .body("message", is(MEMBER_IS_NOT_HOST.getMessage()));
            }

            @Test
            @DisplayName("공지사항을 작성한다")
            void success() {
                스터디_공지사항을_작성한다(hostAccessToken, studyId)
                        .statusCode(OK.value())
                        .body("noticeId", notNullValue(Long.class));
            }
        }

        @Nested
        @DisplayName("스터디 공지사항 수정 API")
        class UpdateNotice {
            private Long noticeId;

            @BeforeEach
            void setUp() {
                noticeId = 스터디_공지사항을_작성한다(hostAccessToken, studyId)
                        .extract()
                        .jsonPath()
                        .getLong("noticeId");
            }

            @Test
            @DisplayName("공지사항을 수정한다")
            void success() {
                작성한_스터디_공지사항을_수정한다(hostAccessToken, studyId, noticeId)
                        .statusCode(NO_CONTENT.value());
            }
        }

        @Nested
        @DisplayName("스터디 공지사항 삭제 API")
        class DeleteNotice {
            private Long noticeId;

            @BeforeEach
            void setUp() {
                noticeId = 스터디_공지사항을_작성한다(hostAccessToken, studyId)
                        .extract()
                        .jsonPath()
                        .getLong("noticeId");
            }

            @Test
            @DisplayName("공지사항을 삭제한다")
            void success() {
                작성한_스터디_공지사항을_삭제한다(hostAccessToken, studyId, noticeId)
                        .statusCode(NO_CONTENT.value());
            }
        }
    }

    @Nested
    @DisplayName("스터디 공지사항 댓글 API")
    class NoticeComment {
        private Long noticeId;

        @BeforeEach
        void setUp() {
            noticeId = 스터디_공지사항을_작성한다(hostAccessToken, studyId)
                    .extract()
                    .jsonPath()
                    .getLong("noticeId");
        }

        @Nested
        @DisplayName("스터디 공지사항 댓글 작성 API")
        class WriteNoticeComment {
            @Test
            @DisplayName("참여자가 아니면 공지사항에 댓글을 작성할 수 없다")
            void memberIsNotParticipant() {
                final String anonymousAccessToken = DUMMY9.회원가입_후_Google_OAuth_로그인을_진행한다().token().accessToken();
                스터디_공지사항에_댓글을_작성한다(anonymousAccessToken, noticeId)
                        .statusCode(ONLY_PARTICIPANT_CAN_WRITE_COMMENT.getStatus().value())
                        .body("errorCode", is(ONLY_PARTICIPANT_CAN_WRITE_COMMENT.getErrorCode()))
                        .body("message", is(ONLY_PARTICIPANT_CAN_WRITE_COMMENT.getMessage()));
            }

            @Test
            @DisplayName("공지사항에 댓글을 작성한다")
            void success() {
                스터디_공지사항에_댓글을_작성한다(hostAccessToken, noticeId)
                        .statusCode(NO_CONTENT.value());
                스터디_공지사항에_댓글을_작성한다(memberAccessToken, noticeId)
                        .statusCode(NO_CONTENT.value());
            }
        }

        @Nested
        @DisplayName("스터디 공지사항 댓글 수정 API")
        class UpdateNoticeComment {
            private Long commentId;

            @BeforeEach
            void setUp() {
                스터디_공지사항에_댓글을_작성한다(hostAccessToken, noticeId);
                commentId = 스터디_공지사항을_조회한다(hostAccessToken, studyId)
                        .extract()
                        .jsonPath()
                        .getLong("result[0].comments[0].id");
            }

            @Test
            @DisplayName("공지사항에 작성한 댓글을 수정한다")
            void success() {
                스터디_공지사항에_작성한_댓글을_수정한다(hostAccessToken, noticeId, commentId)
                        .statusCode(NO_CONTENT.value());
            }
        }

        @Nested
        @DisplayName("스터디 공지사항 댓글 삭제 API")
        class DeleteNoticeComment {
            private Long commentId;

            @BeforeEach
            void setUp() {
                스터디_공지사항에_댓글을_작성한다(hostAccessToken, noticeId);
                commentId = 스터디_공지사항을_조회한다(hostAccessToken, studyId)
                        .extract()
                        .jsonPath()
                        .getLong("result[0].comments[0].id");
            }

            @Test
            @DisplayName("공지사항에 작성한 댓글을 삭제한다")
            void success() {
                스터디_공지사항에_작성한_댓글을_삭제한다(hostAccessToken, noticeId, commentId)
                        .statusCode(NO_CONTENT.value());
            }
        }
    }
}
