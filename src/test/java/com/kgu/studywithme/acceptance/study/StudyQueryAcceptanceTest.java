package com.kgu.studywithme.acceptance.study;

import com.kgu.studywithme.common.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.format.DateTimeFormatter;

import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.사용자에_대한_해당_주차_출석_정보를_수정한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디_공지사항에_댓글을_작성한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디_공지사항을_작성한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디_공지사항을_조회한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디_기본_정보를_조회한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디_리뷰를_작성한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디_리뷰를_조회한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디_신청자를_조회한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디_신청자에_대한_참여를_승인한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디_주차를_생성한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디_주차별_정보를_조회한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디_참여_신청을_한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디_참여자를_조회한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디_출석_정보를_조회한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디를_졸업한다;
import static com.kgu.studywithme.common.fixture.MemberFixture.ANONYMOUS;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY9;
import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.KAFKA;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_1;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_2;
import static com.kgu.studywithme.study.domain.RecruitmentStatus.IN_PROGRESS;
import static com.kgu.studywithme.study.exception.StudyErrorCode.MEMBER_IS_NOT_HOST;
import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.ABSENCE;
import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.ATTENDANCE;
import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.LATE;
import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.NON_ATTENDANCE;
import static com.kgu.studywithme.studyparticipant.exception.StudyParticipantErrorCode.MEMBER_IS_NOT_PARTICIPANT;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.http.HttpStatus.OK;

@DisplayName("[Acceptance Test] 스터디 정보 관련 기능")
public class StudyQueryAcceptanceTest extends AcceptanceTest {
    private Long hostId;
    private String hostAccessToken;

    @BeforeEach
    void setUp() {
        hostId = JIWON.회원가입을_진행한다();
        hostAccessToken = JIWON.로그인을_진행한다().token().accessToken();
    }

    @Nested
    @DisplayName("Public 스터디 정보 조회 API")
    class AboutStudyForAll {
        private Long studyId;

        @BeforeEach
        void setUp() {
            studyId = KAFKA.스터디를_생성한다(hostAccessToken);
        }

        @Nested
        @DisplayName("스터디 기본 정보 조회 API")
        class GetPublicInformation {
            @Test
            @DisplayName("스터디 기본 정보를 조회한다")
            void success() {
                스터디_기본_정보를_조회한다(studyId)
                        .statusCode(OK.value())
                        .body("id", is(studyId.intValue()))
                        .body("name", is(KAFKA.getName().getValue()))
                        .body("description", is(KAFKA.getDescription().getValue()))
                        .body("category", is(KAFKA.getCategory().getName()))
                        .body("thumbnail.name", is(KAFKA.getThumbnail().getImageName()))
                        .body("thumbnail.background", is(KAFKA.getThumbnail().getBackground()))
                        .body("type", is(KAFKA.getType().toString()))
                        .body("location", nullValue())
                        .body("recruitmentStatus", is(IN_PROGRESS.toString()))
                        .body("maxMember", is(KAFKA.getCapacity().getValue()))
                        .body("participantMembers", is(1))
                        .body("minimumAttendanceForGraduation", is(KAFKA.getMinimumAttendanceForGraduation()))
                        .body("remainingOpportunityToUpdateGraduationPolicy", is(3))
                        .body("host.id", is(hostId.intValue()))
                        .body("hashtags", hasSize(KAFKA.getHashtags().size()))
                        .body("hashtags", containsInAnyOrder(KAFKA.getHashtags().toArray()))
                        .body("participants", hasSize(1))
                        .body("participants[0].id", is(hostId.intValue()));

                final Long ghostId = GHOST.회원가입을_진행한다();
                final String ghostAccessToken = GHOST.로그인을_진행한다().token().accessToken();
                GHOST가_스터디에_참여한다(ghostId, ghostAccessToken);
                스터디_기본_정보를_조회한다(studyId)
                        .statusCode(OK.value())
                        .body("participants", hasSize(2))
                        .body("participants[0].id", is(hostId.intValue()))
                        .body("participants[1].id", is(ghostId.intValue()));
            }

            private void GHOST가_스터디에_참여한다(
                    final Long ghostId,
                    final String ghostAccessToken
            ) {
                스터디_참여_신청을_한다(ghostAccessToken, studyId);
                스터디_신청자에_대한_참여를_승인한다(hostAccessToken, studyId, ghostId);
            }
        }

        @Nested
        @DisplayName("스터디 리뷰 조회 API")
        class GetReviewss {
            @Test
            @DisplayName("스터디 리뷰를 조회한다")
            void success() {
                스터디_리뷰를_조회한다(studyId)
                        .statusCode(OK.value())
                        .body("reviews", hasSize(0))
                        .body("graduateCount", is(0));

                final Long ghostId = GHOST.회원가입을_진행한다();
                final String ghostAccessToken = GHOST.로그인을_진행한다().token().accessToken();
                GHOST가_스터디를_졸업한다(ghostId, ghostAccessToken);
                스터디_리뷰를_조회한다(studyId)
                        .statusCode(OK.value())
                        .body("reviews", hasSize(0))
                        .body("graduateCount", is(1));

                스터디_리뷰를_작성한다(ghostAccessToken, studyId);
                스터디_리뷰를_조회한다(studyId)
                        .statusCode(OK.value())
                        .body("reviews", hasSize(1))
                        .body("graduateCount", is(1));
            }

            private void GHOST가_스터디를_졸업한다(
                    final Long ghostId,
                    final String ghostAccessToken
            ) {
                스터디_참여_신청을_한다(ghostAccessToken, studyId);
                스터디_신청자에_대한_참여를_승인한다(hostAccessToken, studyId, ghostId);
                스터디를_졸업한다(ghostAccessToken, studyId);
            }
        }

        @Nested
        @DisplayName("스터디 참여자 조회 API")
        class GetParticipant {
            private Long memberId;

            @BeforeEach
            void setUp() {
                memberId = GHOST.회원가입을_진행한다();
                final String memberAccessToken = GHOST.로그인을_진행한다().token().accessToken();
                스터디_참여_신청을_한다(memberAccessToken, studyId);
            }

            @Test
            @DisplayName("스터디 참여자를 조회한다")
            void success() {
                스터디_참여자를_조회한다(studyId)
                        .statusCode(OK.value())
                        .body("host.id", is(hostId.intValue()))
                        .body("participants", hasSize(0));

                스터디_신청자에_대한_참여를_승인한다(hostAccessToken, studyId, memberId);
                스터디_참여자를_조회한다(studyId)
                        .statusCode(OK.value())
                        .body("host.id", is(hostId.intValue()))
                        .body("participants", hasSize(1))
                        .body("participants[0].id", is(memberId.intValue()));
            }
        }
    }

    @Nested
    @DisplayName("참여자 전용 스터디 정보")
    class AboutStudyForOnlyParticipant {
        private Long studyId;

        @BeforeEach
        void setUp() {
            studyId = SPRING.스터디를_생성한다(hostAccessToken);
        }

        @Nested
        @DisplayName("스터디 신청자 조회 API")
        class GetAppliers {
            private Long idOfMemberA;
            private String accessTokenOfMemberA;
            private Long idOfMemberB;
            private String accessTokenOfMemberB;

            @BeforeEach
            void setUp() {
                idOfMemberA = GHOST.회원가입을_진행한다();
                accessTokenOfMemberA = GHOST.로그인을_진행한다().token().accessToken();
                idOfMemberB = ANONYMOUS.회원가입을_진행한다();
                accessTokenOfMemberB = ANONYMOUS.로그인을_진행한다().token().accessToken();
            }

            @Test
            @DisplayName("스터디 팀장이 아니면 신청자를 조회할 수 없다")
            void memberIsNotHost() {
                스터디_신청자를_조회한다(accessTokenOfMemberA, studyId)
                        .statusCode(MEMBER_IS_NOT_HOST.getStatus().value())
                        .body("errorCode", is(MEMBER_IS_NOT_HOST.getErrorCode()))
                        .body("message", is(MEMBER_IS_NOT_HOST.getMessage()));
            }

            @Test
            @DisplayName("스터디 신청자를 조회한다")
            void success() {
                스터디_신청자를_조회한다(hostAccessToken, studyId)
                        .statusCode(OK.value())
                        .body("result", hasSize(0));

                스터디_참여_신청을_한다(accessTokenOfMemberA, studyId);
                스터디_신청자를_조회한다(hostAccessToken, studyId)
                        .statusCode(OK.value())
                        .body("result", hasSize(1))
                        .body("result[0].id", is(idOfMemberA.intValue()));

                스터디_참여_신청을_한다(accessTokenOfMemberB, studyId);
                스터디_신청자를_조회한다(hostAccessToken, studyId)
                        .statusCode(OK.value())
                        .body("result", hasSize(2))
                        .body("result[0].id", is(idOfMemberB.intValue()))
                        .body("result[1].id", is(idOfMemberA.intValue()));
            }
        }

        @Nested
        @DisplayName("스터디 공지사항 조회 API")
        class GetNotice {
            private Long memberId;
            private String memberAccessToken;

            @BeforeEach
            void setUp() {
                memberId = GHOST.회원가입을_진행한다();
                memberAccessToken = GHOST.로그인을_진행한다().token().accessToken();
                스터디_참여_신청을_한다(memberAccessToken, studyId);
            }

            @Test
            @DisplayName("스터디 참여자가 아니면 공지사항을 조회할 수 없다")
            void memberIsNotParticipant() {
                스터디_공지사항을_조회한다(memberAccessToken, studyId)
                        .statusCode(MEMBER_IS_NOT_PARTICIPANT.getStatus().value())
                        .body("errorCode", is(MEMBER_IS_NOT_PARTICIPANT.getErrorCode()))
                        .body("message", is(MEMBER_IS_NOT_PARTICIPANT.getMessage()));
            }

            @Test
            @DisplayName("스터디 공지사항을 조회한다")
            void success() {
                스터디_공지사항을_조회한다(hostAccessToken, studyId)
                        .statusCode(OK.value())
                        .body("result", hasSize(0));

                final Long noticeId = 스터디_공지사항을_작성한다(hostAccessToken, studyId)
                        .extract()
                        .jsonPath()
                        .getLong("noticeId");
                System.out.println("ID = " + noticeId);
                스터디_공지사항을_조회한다(hostAccessToken, studyId)
                        .statusCode(OK.value())
                        .body("result", hasSize(1))
                        .body("result[0].id", is(noticeId.intValue()))
                        .body("result[0].writer.id", is(hostId.intValue()))
                        .body("result[0].comments", hasSize(0));

                스터디_신청자에_대한_참여를_승인한다(hostAccessToken, studyId, memberId);
                스터디_공지사항에_댓글을_작성한다(memberAccessToken, noticeId);
                스터디_공지사항을_조회한다(hostAccessToken, studyId)
                        .statusCode(OK.value())
                        .body("result", hasSize(1))
                        .body("result[0].id", is(noticeId.intValue()))
                        .body("result[0].writer.id", is(hostId.intValue()))
                        .body("result[0].comments", hasSize(1))
                        .body("result[0].comments[0].writer.id", is(memberId.intValue()));
            }
        }

        @Nested
        @DisplayName("스터디 출석 정보 조회 API")
        class GetAttendanceInformation {
            private Long memberId;

            @BeforeEach
            void setUp() {
                memberId = GHOST.회원가입을_진행한다();
                final String memberAccessToken = GHOST.로그인을_진행한다().token().accessToken();

                스터디_참여_신청을_한다(memberAccessToken, studyId);
                스터디_신청자에_대한_참여를_승인한다(hostAccessToken, studyId, memberId);

                스터디_주차를_생성한다(hostAccessToken, studyId, STUDY_WEEKLY_1);
                스터디_주차를_생성한다(hostAccessToken, studyId, STUDY_WEEKLY_2);
            }

            @Test
            @DisplayName("스터디 참여자가 아니면 출석 정보를 조회할 수 없다")
            void memberIsNotParticipant() {
                final String anonymousAccessToken = DUMMY9.회원가입_후_Google_OAuth_로그인을_진행한다().token().accessToken();
                스터디_출석_정보를_조회한다(anonymousAccessToken, studyId)
                        .statusCode(MEMBER_IS_NOT_PARTICIPANT.getStatus().value())
                        .body("errorCode", is(MEMBER_IS_NOT_PARTICIPANT.getErrorCode()))
                        .body("message", is(MEMBER_IS_NOT_PARTICIPANT.getMessage()));
            }

            @Test
            @DisplayName("스터디 출석 정보를 조회한다")
            void success() {
                스터디_출석_정보를_조회한다(hostAccessToken, studyId)
                        .statusCode(OK.value())
                        .body("result", hasSize(2))
                        .body("result[0].member.id", is(hostId.intValue()))
                        .body("result[0].summaries[0].week", is(1))
                        .body("result[0].summaries[0].attendanceStatus", is(NON_ATTENDANCE.getValue()))
                        .body("result[0].summaries[1].week", is(2))
                        .body("result[0].summaries[1].attendanceStatus", is(NON_ATTENDANCE.getValue()))
                        .body("result[1].member.id", is(memberId.intValue()))
                        .body("result[1].summaries[0].week", is(1))
                        .body("result[1].summaries[0].attendanceStatus", is(NON_ATTENDANCE.getValue()))
                        .body("result[1].summaries[1].week", is(2))
                        .body("result[1].summaries[1].attendanceStatus", is(NON_ATTENDANCE.getValue()));

                사용자에_대한_해당_주차_출석_정보를_수정한다(hostAccessToken, studyId, hostId, STUDY_WEEKLY_1.getWeek(), ATTENDANCE);
                사용자에_대한_해당_주차_출석_정보를_수정한다(hostAccessToken, studyId, memberId, STUDY_WEEKLY_1.getWeek(), LATE);
                스터디_출석_정보를_조회한다(hostAccessToken, studyId)
                        .statusCode(OK.value())
                        .body("result", hasSize(2))
                        .body("result[0].member.id", is(hostId.intValue()))
                        .body("result[0].summaries[0].week", is(1))
                        .body("result[0].summaries[0].attendanceStatus", is(ATTENDANCE.getValue()))
                        .body("result[0].summaries[1].week", is(2))
                        .body("result[0].summaries[1].attendanceStatus", is(NON_ATTENDANCE.getValue()))
                        .body("result[1].member.id", is(memberId.intValue()))
                        .body("result[1].summaries[0].week", is(1))
                        .body("result[1].summaries[0].attendanceStatus", is(LATE.getValue()))
                        .body("result[1].summaries[1].week", is(2))
                        .body("result[1].summaries[1].attendanceStatus", is(NON_ATTENDANCE.getValue()));

                사용자에_대한_해당_주차_출석_정보를_수정한다(hostAccessToken, studyId, hostId, STUDY_WEEKLY_2.getWeek(), LATE);
                사용자에_대한_해당_주차_출석_정보를_수정한다(hostAccessToken, studyId, memberId, STUDY_WEEKLY_2.getWeek(), ABSENCE);
                스터디_출석_정보를_조회한다(hostAccessToken, studyId)
                        .statusCode(OK.value())
                        .body("result", hasSize(2))
                        .body("result[0].member.id", is(hostId.intValue()))
                        .body("result[0].summaries[0].week", is(1))
                        .body("result[0].summaries[0].attendanceStatus", is(ATTENDANCE.getValue()))
                        .body("result[0].summaries[1].week", is(2))
                        .body("result[0].summaries[1].attendanceStatus", is(LATE.getValue()))
                        .body("result[1].member.id", is(memberId.intValue()))
                        .body("result[1].summaries[0].week", is(1))
                        .body("result[1].summaries[0].attendanceStatus", is(LATE.getValue()))
                        .body("result[1].summaries[1].week", is(2))
                        .body("result[1].summaries[1].attendanceStatus", is(ABSENCE.getValue()));
            }
        }

        @Nested
        @DisplayName("스터디 주차별 정보 조회 API")
        class GetWeekly {
            private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:00");

            @Test
            @DisplayName("스터디 참여자가 아니면 주차별 정보를 조회할 수 없다")
            void memberIsNotParticipant() {
                final String anonymousAccessToken = DUMMY9.회원가입_후_Google_OAuth_로그인을_진행한다().token().accessToken();
                스터디_주차별_정보를_조회한다(anonymousAccessToken, studyId)
                        .statusCode(MEMBER_IS_NOT_PARTICIPANT.getStatus().value())
                        .body("errorCode", is(MEMBER_IS_NOT_PARTICIPANT.getErrorCode()))
                        .body("message", is(MEMBER_IS_NOT_PARTICIPANT.getMessage()));
            }

            @Test
            @DisplayName("스터디 주차별 정보를 조회한다")
            void success() {
                스터디_주차별_정보를_조회한다(hostAccessToken, studyId)
                        .statusCode(OK.value())
                        .body("result", hasSize(0));

                final Long weeklyId = 스터디_주차를_생성한다(hostAccessToken, studyId, STUDY_WEEKLY_1)
                        .extract()
                        .jsonPath()
                        .getLong("weeklyId");
                스터디_주차별_정보를_조회한다(hostAccessToken, studyId)
                        .statusCode(OK.value())
                        .body("result", hasSize(1))
                        .body("result[0].id", is(weeklyId.intValue()))
                        .body("result[0].title", is(STUDY_WEEKLY_1.getTitle()))
                        .body("result[0].week", is(STUDY_WEEKLY_1.getWeek()))
                        .body("result[0].period.startDate", is(STUDY_WEEKLY_1.getPeriod().getStartDate().format(DATE_TIME_FORMATTER)))
                        .body("result[0].period.endDate", is(STUDY_WEEKLY_1.getPeriod().getEndDate().format(DATE_TIME_FORMATTER)))
                        .body("result[0].assignmentExists", is(STUDY_WEEKLY_1.isAssignmentExists()))
                        .body("result[0].autoAttendance", is(STUDY_WEEKLY_1.isAutoAttendance()))
                        .body("result[0].creator.id", is(weeklyId.intValue()))
                        .body("result[0].attachments", hasSize(STUDY_WEEKLY_1.getAttachments().size()));
            }
        }
    }
}
