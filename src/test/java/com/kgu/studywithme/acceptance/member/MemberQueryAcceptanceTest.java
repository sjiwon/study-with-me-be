package com.kgu.studywithme.acceptance.member;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.common.AcceptanceTest;
import com.kgu.studywithme.common.config.DatabaseCleanerEachCallbackExtension;
import com.kgu.studywithme.member.infrastructure.query.dto.GraduatedStudy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.stream.Stream;

import static com.kgu.studywithme.acceptance.favorite.FavoriteAcceptanceFixture.스터디를_찜_등록한다;
import static com.kgu.studywithme.acceptance.member.MemberAcceptanceFixture.사용자_Private_정보를_조회한다;
import static com.kgu.studywithme.acceptance.member.MemberAcceptanceFixture.사용자_Public_정보를_조회한다;
import static com.kgu.studywithme.acceptance.member.MemberAcceptanceFixture.사용자가_받은_리뷰를_조회한다;
import static com.kgu.studywithme.acceptance.member.MemberAcceptanceFixture.사용자가_신청한_스터디를_조회한다;
import static com.kgu.studywithme.acceptance.member.MemberAcceptanceFixture.사용자가_졸업한_스터디를_조회한다;
import static com.kgu.studywithme.acceptance.member.MemberAcceptanceFixture.사용자가_찜한_스터디를_조회한다;
import static com.kgu.studywithme.acceptance.member.MemberAcceptanceFixture.사용자가_참여하고_있는_스터디를_조회한다;
import static com.kgu.studywithme.acceptance.member.MemberAcceptanceFixture.사용자의_스터디_출석률을_조회한다;
import static com.kgu.studywithme.acceptance.member.MemberAcceptanceFixture.해당_사용자에게_리뷰를_작성한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.사용자에_대한_해당_주차_출석_정보를_수정한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디_신청자에_대한_참여를_승인한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디_주차를_생성한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디_참여_신청을_한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디를_졸업한다;
import static com.kgu.studywithme.common.fixture.MemberFixture.ANONYMOUS;
import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.KAFKA;
import static com.kgu.studywithme.common.fixture.StudyFixture.LINE_INTERVIEW;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_1;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_2;
import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.ATTENDANCE;
import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.LATE;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(DatabaseCleanerEachCallbackExtension.class)
@DisplayName("[Acceptance Test] 사용자 정보 관련 기능")
public class MemberQueryAcceptanceTest extends AcceptanceTest {
    private Long memberId;
    private String memberAccessToken;

    @BeforeEach
    void setUp() {
        memberId = JIWON.회원가입을_진행한다();
        memberAccessToken = JIWON.로그인을_진행한다().token().accessToken();
    }

    @Nested
    @DisplayName("Only 사용자 정보 API")
    class AboutMember {
        @Nested
        @DisplayName("사용자 Private 정보 조회 API")
        class GetPrivateInformation {
            @Test
            @DisplayName("사용자 Private 정보를 조회한다")
            void success() {
                사용자_Private_정보를_조회한다(memberAccessToken)
                        .statusCode(OK.value())
                        .body("id", is(memberId.intValue()))
                        .body("name", is(JIWON.getName()))
                        .body("nickname", is(JIWON.getNickname().getValue()))
                        .body("email", is(JIWON.getEmail().getValue()))
                        .body("emailOptIn", is(JIWON.getEmail().isEmailOptIn()))
                        .body("birth", is(JIWON.getBirth().toString()))
                        .body("phone", is(JIWON.getPhone().getValue()))
                        .body("gender", is(JIWON.getGender().getValue()))
                        .body("address.province", is(JIWON.getAddress().getProvince()))
                        .body("address.city", is(JIWON.getAddress().getCity()))
                        .body("score", is(80))
                        .body("interests", containsInAnyOrder(
                                JIWON.getInterests()
                                        .stream()
                                        .map(Category::getName)
                                        .toList()
                                        .toArray()
                        ));
            }
        }

        @Nested
        @DisplayName("사용자 Public 정보 조회 API")
        class GetPublicInformation {
            @Test
            @DisplayName("사용자 Public 정보를 조회한다")
            void success() {
                사용자_Public_정보를_조회한다(memberId)
                        .statusCode(OK.value())
                        .body("id", is(memberId.intValue()))
                        .body("name", is(JIWON.getName()))
                        .body("nickname", is(JIWON.getNickname().getValue()))
                        .body("email", is(JIWON.getEmail().getValue()))
                        .body("birth", is(JIWON.getBirth().toString()))
                        .body("gender", is(JIWON.getGender().getValue()))
                        .body("address.province", is(JIWON.getAddress().getProvince()))
                        .body("address.city", is(JIWON.getAddress().getCity()))
                        .body("score", is(80))
                        .body("interests", containsInAnyOrder(
                                JIWON.getInterests()
                                        .stream()
                                        .map(Category::getName)
                                        .toList()
                                        .toArray()
                        ));
            }
        }

        @Nested
        @DisplayName("사용자가 받은 리뷰 조회 API")
        class GetReviews {
            private String hostAccessToken;
            private String participantAccessToken;

            @BeforeEach
            void setUp() {
                hostAccessToken = GHOST.회원가입_후_Google_OAuth_로그인을_진행한다().token().accessToken();
                final Long participantId = ANONYMOUS.회원가입을_진행한다();
                participantAccessToken = ANONYMOUS.로그인을_진행한다().token().accessToken();

                final Long studyId = SPRING.스터디를_생성한다(hostAccessToken);
                스터디_참여_신청을_한다(memberAccessToken, studyId);
                스터디_참여_신청을_한다(participantAccessToken, studyId);
                스터디_신청자에_대한_참여를_승인한다(hostAccessToken, studyId, memberId);
                스터디_신청자에_대한_참여를_승인한다(hostAccessToken, studyId, participantId);
                스터디_주차를_생성한다(hostAccessToken, studyId, STUDY_WEEKLY_1);
            }

            @Test
            @DisplayName("사용자 Public 정보를 조회한다")
            void success() {
                사용자가_받은_리뷰를_조회한다(memberId)
                        .statusCode(OK.value())
                        .body("result", hasSize(0));

                해당_사용자에게_리뷰를_작성한다(hostAccessToken, memberId);
                사용자가_받은_리뷰를_조회한다(memberId)
                        .statusCode(OK.value())
                        .body("result", hasSize(1));

                해당_사용자에게_리뷰를_작성한다(participantAccessToken, memberId);
                사용자가_받은_리뷰를_조회한다(memberId)
                        .statusCode(OK.value())
                        .body("result", hasSize(2));
            }
        }
    }

    @Nested
    @DisplayName("사용자 + 스터디 정보 API")
    class AboutMemberAndStudy {
        private String hostAccessToken;
        private List<Long> studies;

        @BeforeEach
        void setUp() {
            hostAccessToken = GHOST.회원가입_후_Google_OAuth_로그인을_진행한다().token().accessToken();
            studies = Stream.of(LINE_INTERVIEW, SPRING, KAFKA)
                    .map(study -> study.스터디를_생성한다(hostAccessToken))
                    .toList();
            studies.forEach(studyId -> 스터디_참여_신청을_한다(memberAccessToken, studyId));
        }

        @Nested
        @DisplayName("사용자가 신청한 스터디 조회 API")
        class GetApplyStudy {
            @Test
            @DisplayName("사용자가 신청한 스터디를 조회한다")
            void success() {
                사용자가_신청한_스터디를_조회한다(memberAccessToken)
                        .statusCode(OK.value())
                        .body("result", hasSize(3))
                        .body("result[0].id", is(studies.get(2).intValue()))
                        .body("result[1].id", is(studies.get(1).intValue()))
                        .body("result[2].id", is(studies.get(0).intValue()));
            }
        }

        @Nested
        @DisplayName("사용자가 찜한 스터디 조회 API")
        class GetFavoriteStudy {
            @Test
            @DisplayName("사용자가 찜한 스터디를 조회한다")
            void success() {
                사용자가_찜한_스터디를_조회한다(memberAccessToken)
                        .statusCode(OK.value())
                        .body("result", hasSize(0));

                스터디를_찜_등록한다(memberAccessToken, studies.get(0));
                스터디를_찜_등록한다(memberAccessToken, studies.get(2));
                사용자가_찜한_스터디를_조회한다(memberAccessToken)
                        .statusCode(OK.value())
                        .body("result", hasSize(2))
                        .body("result[0].id", is(studies.get(2).intValue()))
                        .body("result[1].id", is(studies.get(0).intValue()));

                스터디를_찜_등록한다(memberAccessToken, studies.get(1));
                사용자가_찜한_스터디를_조회한다(memberAccessToken)
                        .statusCode(OK.value())
                        .body("result", hasSize(3))
                        .body("result[0].id", is(studies.get(1).intValue()))
                        .body("result[1].id", is(studies.get(2).intValue()))
                        .body("result[2].id", is(studies.get(0).intValue()));
            }
        }

        @Nested
        @DisplayName("사용자가 참여하고 있는 스터디 조회 API")
        class GetParticipateStudy {
            @Test
            @DisplayName("사용자가 참여하고 있는 스터디를 조회한다")
            void success() {
                사용자가_참여하고_있는_스터디를_조회한다(memberId)
                        .statusCode(OK.value())
                        .body("result", hasSize(0));

                스터디_신청자에_대한_참여를_승인한다(hostAccessToken, studies.get(0), memberId);
                스터디_신청자에_대한_참여를_승인한다(hostAccessToken, studies.get(2), memberId);
                사용자가_참여하고_있는_스터디를_조회한다(memberId)
                        .statusCode(OK.value())
                        .body("result", hasSize(2))
                        .body("result[0].id", is(studies.get(2).intValue()))
                        .body("result[1].id", is(studies.get(0).intValue()));

                스터디_신청자에_대한_참여를_승인한다(hostAccessToken, studies.get(1), memberId);
                사용자가_참여하고_있는_스터디를_조회한다(memberId)
                        .statusCode(OK.value())
                        .body("result", hasSize(3))
                        .body("result[0].id", is(studies.get(2).intValue()))
                        .body("result[1].id", is(studies.get(1).intValue()))
                        .body("result[2].id", is(studies.get(0).intValue()));
            }
        }

        @Nested
        @DisplayName("사용자가 졸업한 스터디 조회 API")
        class GetGraduatedStudy {
            @BeforeEach
            void setUp() {
                스터디_신청자에_대한_참여를_승인한다(hostAccessToken, studies.get(0), memberId);
                스터디_신청자에_대한_참여를_승인한다(hostAccessToken, studies.get(1), memberId);
                스터디_신청자에_대한_참여를_승인한다(hostAccessToken, studies.get(2), memberId);
            }

            @Test
            @DisplayName("사용자가 졸업한 스터디를 조회한다")
            void success() {
                사용자가_졸업한_스터디를_조회한다(memberId)
                        .statusCode(OK.value())
                        .body("result", hasSize(0));

                스터디를_졸업한다(memberAccessToken, studies.get(2));
                사용자가_졸업한_스터디를_조회한다(memberId)
                        .statusCode(OK.value())
                        .body("result", hasSize(1))
                        .body("result[0].id", is(studies.get(2).intValue()))
                        .body("result[0].review", nullValue(GraduatedStudy.WrittenReview.class));
            }
        }

        @Nested
        @DisplayName("사용자 출석률 조회 API")
        class GetAttendanceRatio {
            @BeforeEach
            void setUp() {
                스터디_신청자에_대한_참여를_승인한다(hostAccessToken, studies.get(0), memberId);
                스터디_주차를_생성한다(hostAccessToken, studies.get(0), STUDY_WEEKLY_1);
                스터디_주차를_생성한다(hostAccessToken, studies.get(0), STUDY_WEEKLY_2);
            }

            @Test
            @DisplayName("사용자 출석률을 조회한다")
            void success() {
                사용자의_스터디_출석률을_조회한다(memberId)
                        .statusCode(OK.value())
                        .body("result", hasSize(4))
                        .body("result[0].status", is("ATTENDANCE"))
                        .body("result[0].count", is(0))
                        .body("result[1].status", is("LATE"))
                        .body("result[1].count", is(0))
                        .body("result[2].status", is("ABSENCE"))
                        .body("result[2].count", is(0))
                        .body("result[3].status", is("NON_ATTENDANCE"))
                        .body("result[3].count", is(2));

                사용자에_대한_해당_주차_출석_정보를_수정한다(hostAccessToken, studies.get(0), memberId, STUDY_WEEKLY_1.getWeek(), LATE);
                사용자의_스터디_출석률을_조회한다(memberId)
                        .statusCode(OK.value())
                        .body("result", hasSize(4))
                        .body("result[0].status", is("ATTENDANCE"))
                        .body("result[0].count", is(0))
                        .body("result[1].status", is("LATE"))
                        .body("result[1].count", is(1))
                        .body("result[2].status", is("ABSENCE"))
                        .body("result[2].count", is(0))
                        .body("result[3].status", is("NON_ATTENDANCE"))
                        .body("result[3].count", is(1));

                사용자에_대한_해당_주차_출석_정보를_수정한다(hostAccessToken, studies.get(0), memberId, STUDY_WEEKLY_2.getWeek(), ATTENDANCE);
                사용자의_스터디_출석률을_조회한다(memberId)
                        .statusCode(OK.value())
                        .body("result", hasSize(4))
                        .body("result[0].status", is("ATTENDANCE"))
                        .body("result[0].count", is(1))
                        .body("result[1].status", is("LATE"))
                        .body("result[1].count", is(1))
                        .body("result[2].status", is("ABSENCE"))
                        .body("result[2].count", is(0))
                        .body("result[3].status", is("NON_ATTENDANCE"))
                        .body("result[3].count", is(0));
            }
        }
    }
}
