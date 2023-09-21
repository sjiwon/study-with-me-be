package com.kgu.studywithme.acceptance.study;

import com.kgu.studywithme.auth.domain.model.AuthMember;
import com.kgu.studywithme.auth.domain.model.AuthToken;
import com.kgu.studywithme.common.AcceptanceTest;
import com.kgu.studywithme.common.config.DatabaseCleanerAllCallbackExtension;
import com.kgu.studywithme.common.fixture.MemberFixture;
import com.kgu.studywithme.common.fixture.StudyFixture;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kgu.studywithme.acceptance.favorite.FavoriteAcceptanceFixture.스터디를_찜_등록한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.관심사로_스터디를_조회한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디_리뷰를_작성한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디_신청자에_대한_참여를_승인한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디_참여_신청을_한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.스터디를_졸업한다;
import static com.kgu.studywithme.acceptance.study.StudyAcceptanceFixture.카테고리로_스터디를_조회한다;
import static com.kgu.studywithme.category.domain.model.Category.PROGRAMMING;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY1;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY2;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY3;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY4;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY5;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.JSP;
import static com.kgu.studywithme.common.fixture.StudyFixture.LINE_INTERVIEW;
import static com.kgu.studywithme.common.fixture.StudyFixture.OOP;
import static com.kgu.studywithme.common.fixture.StudyFixture.RABBITMQ;
import static com.kgu.studywithme.common.fixture.StudyFixture.REDIS;
import static com.kgu.studywithme.common.fixture.StudyFixture.TOEIC;
import static com.kgu.studywithme.study.utils.search.SearchSortType.DATE;
import static com.kgu.studywithme.study.utils.search.SearchSortType.FAVORITE;
import static com.kgu.studywithme.study.utils.search.SearchSortType.REVIEW;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(DatabaseCleanerAllCallbackExtension.class)
@DisplayName("[Acceptance Test] 스터디 조회 관련 기능")
public class StudySearchAcceptanceTest extends AcceptanceTest {
    private static String hostAccessToken;
    private static Map<StudyFixture, Long> studies;
    private static List<Long> members;

    @BeforeAll
    static void setUp() {
        hostAccessToken = JIWON.회원가입_후_Google_OAuth_로그인을_진행한다().token().accessToken();
        studies = Stream.of(RABBITMQ, OOP, REDIS, JSP, TOEIC, LINE_INTERVIEW)
                .collect(Collectors.toMap(
                        study -> study,
                        study -> study.스터디를_생성한다(hostAccessToken)
                ));
        members = Stream.of(DUMMY1, DUMMY2, DUMMY3, DUMMY4, DUMMY5)
                .map(MemberFixture::회원가입을_진행한다)
                .toList();

        final List<String> participantAccessTokens = Stream.of(DUMMY1, DUMMY2, DUMMY3, DUMMY4, DUMMY5)
                .map(MemberFixture::로그인을_진행한다)
                .map(AuthMember::token)
                .map(AuthToken::accessToken)
                .toList();

        for (StudyFixture fixture : studies.keySet()) {
            for (int i = 0; i < members.size(); i++) {
                스터디_참여_신청을_한다(participantAccessTokens.get(i), studies.get(fixture));
                스터디_신청자에_대한_참여를_승인한다(hostAccessToken, studies.get(fixture), members.get(i));
            }
        }

        // Favorite
        스터디를_찜_등록한다(participantAccessTokens.get(0), studies.get(RABBITMQ)); // studies0 -> duumy1, dummy3, dummy5
        스터디를_찜_등록한다(participantAccessTokens.get(2), studies.get(RABBITMQ));
        스터디를_찜_등록한다(participantAccessTokens.get(4), studies.get(RABBITMQ));

        스터디를_찜_등록한다(participantAccessTokens.get(1), studies.get(REDIS)); // studies2 -> dummy2, dummy4
        스터디를_찜_등록한다(participantAccessTokens.get(3), studies.get(REDIS));

        스터디를_찜_등록한다(participantAccessTokens.get(1), studies.get(TOEIC)); // studies4 -> dummy2, dummy3, dummy5
        스터디를_찜_등록한다(participantAccessTokens.get(2), studies.get(TOEIC));
        스터디를_찜_등록한다(participantAccessTokens.get(4), studies.get(TOEIC));

        스터디를_찜_등록한다(participantAccessTokens.get(0), studies.get(LINE_INTERVIEW)); // studies5 -> dummy1, dummy2, dummy3, dummy4, dummy5
        스터디를_찜_등록한다(participantAccessTokens.get(1), studies.get(LINE_INTERVIEW));
        스터디를_찜_등록한다(participantAccessTokens.get(2), studies.get(LINE_INTERVIEW));
        스터디를_찜_등록한다(participantAccessTokens.get(3), studies.get(LINE_INTERVIEW));
        스터디를_찜_등록한다(participantAccessTokens.get(4), studies.get(LINE_INTERVIEW));

        // Graduate
        스터디를_졸업한다(participantAccessTokens.get(0), studies.get(RABBITMQ)); // studies0 -> dummy1, dummy2
        스터디_리뷰를_작성한다(participantAccessTokens.get(0), studies.get(RABBITMQ));
        스터디를_졸업한다(participantAccessTokens.get(1), studies.get(RABBITMQ));
        스터디_리뷰를_작성한다(participantAccessTokens.get(1), studies.get(RABBITMQ));

        스터디를_졸업한다(participantAccessTokens.get(1), studies.get(OOP)); // studies1 - dummy2
        스터디_리뷰를_작성한다(participantAccessTokens.get(1), studies.get(OOP));

        스터디를_졸업한다(participantAccessTokens.get(2), studies.get(REDIS)); // studies2 - dummy3
        스터디_리뷰를_작성한다(participantAccessTokens.get(2), studies.get(REDIS));

        스터디를_졸업한다(participantAccessTokens.get(1), studies.get(TOEIC)); // studies4 - dummy2, dummy4
        스터디_리뷰를_작성한다(participantAccessTokens.get(1), studies.get(TOEIC));
        스터디를_졸업한다(participantAccessTokens.get(3), studies.get(TOEIC));
        스터디_리뷰를_작성한다(participantAccessTokens.get(3), studies.get(TOEIC));

        스터디를_졸업한다(participantAccessTokens.get(0), studies.get(LINE_INTERVIEW)); // studies5 - dummy1, dummy5
        스터디_리뷰를_작성한다(participantAccessTokens.get(0), studies.get(LINE_INTERVIEW));
        스터디를_졸업한다(participantAccessTokens.get(4), studies.get(LINE_INTERVIEW));
        스터디_리뷰를_작성한다(participantAccessTokens.get(4), studies.get(LINE_INTERVIEW));
    }

    @Nested
    @DisplayName("카테고리 기반 스터디 검색")
    class QueryStudyByCategory {
        @Nested
        @DisplayName("등록 날짜 기준")
        class Date {
            @Test
            @DisplayName("등록 최신 순으로 프로그래밍 스터디를 조회한다")
            void success() {
                final ValidatableResponse response = 카테고리로_스터디를_조회한다(PROGRAMMING.getId(), DATE.getValue(), 0)
                        .statusCode(OK.value());
                assertStudiesMatch(
                        response,
                        List.of(JSP, REDIS, OOP, RABBITMQ),
                        List.of(6, 5, 5, 4),
                        List.of(
                                List.of(),
                                List.of(members.get(1), members.get(3)),
                                List.of(),
                                List.of(members.get(0), members.get(2), members.get(4))
                        ),
                        false
                );
            }
        }

        @Nested
        @DisplayName("좋아요 수 기준")
        class Favorite {
            @Test
            @DisplayName("좋아요 많은 순으로 프로그래밍 스터디를 조회한다")
            void success() {
                final ValidatableResponse response = 카테고리로_스터디를_조회한다(PROGRAMMING.getId(), FAVORITE.getValue(), 0)
                        .statusCode(OK.value());
                assertStudiesMatch(
                        response,
                        List.of(RABBITMQ, REDIS, JSP, OOP),
                        List.of(4, 5, 6, 5),
                        List.of(
                                List.of(members.get(0), members.get(2), members.get(4)),
                                List.of(members.get(1), members.get(3)),
                                List.of(),
                                List.of()
                        ),
                        false
                );
            }
        }

        @Nested
        @DisplayName("리뷰 수 기준")
        class Reviews {
            @Test
            @DisplayName("리뷰 많은 순으로 프로그래밍 스터디를 조회한다")
            void success() {
                final ValidatableResponse response = 카테고리로_스터디를_조회한다(PROGRAMMING.getId(), REVIEW.getValue(), 0)
                        .statusCode(OK.value());
                assertStudiesMatch(
                        response,
                        List.of(RABBITMQ, REDIS, OOP, JSP),
                        List.of(4, 5, 5, 6),
                        List.of(
                                List.of(members.get(0), members.get(2), members.get(4)),
                                List.of(members.get(1), members.get(3)),
                                List.of(),
                                List.of()
                        ),
                        false
                );
            }
        }
    }

    @Nested
    @DisplayName("관심사 기반 스터디 검색 [Host -> LANGUAGE, INTERVIEW, PROGRAMMING]")
    class QueryStudyByRecommend {
        @Nested
        @DisplayName("등록 날짜 기준")
        class Date {
            @Test
            @DisplayName("등록 최신 순으로 스터디를 조회한다")
            void success() {
                final ValidatableResponse response = 관심사로_스터디를_조회한다(hostAccessToken, DATE.getValue(), 0)
                        .statusCode(OK.value());
                assertStudiesMatch(
                        response,
                        List.of(LINE_INTERVIEW, TOEIC, JSP, REDIS, OOP, RABBITMQ),
                        List.of(4, 4, 6, 5, 5, 4),
                        List.of(
                                List.of(members.get(0), members.get(1), members.get(2), members.get(3), members.get(4)),
                                List.of(members.get(1), members.get(2), members.get(4)),
                                List.of(),
                                List.of(members.get(1), members.get(3)),
                                List.of(),
                                List.of(members.get(0), members.get(2), members.get(4))
                        ),
                        false
                );
            }
        }

        @Nested
        @DisplayName("좋아요 수 기준")
        class Favorite {
            @Test
            @DisplayName("좋아요 많은 순으로 스터디를 조회한다")
            void success() {
                final ValidatableResponse response = 관심사로_스터디를_조회한다(hostAccessToken, FAVORITE.getValue(), 0)
                        .statusCode(OK.value());
                assertStudiesMatch(
                        response,
                        List.of(LINE_INTERVIEW, TOEIC, RABBITMQ, REDIS, JSP, OOP),
                        List.of(4, 4, 4, 5, 6, 5),
                        List.of(
                                List.of(members.get(0), members.get(1), members.get(2), members.get(3), members.get(4)),
                                List.of(members.get(1), members.get(2), members.get(4)),
                                List.of(members.get(0), members.get(2), members.get(4)),
                                List.of(members.get(1), members.get(3)),
                                List.of(),
                                List.of()
                        ),
                        false
                );
            }
        }

        @Nested
        @DisplayName("리뷰 수 기준")
        class Reviews {
            @Test
            @DisplayName("리뷰 많은 순으로 스터디를 조회한다")
            void success() {
                final ValidatableResponse response = 관심사로_스터디를_조회한다(hostAccessToken, REVIEW.getValue(), 0)
                        .statusCode(OK.value());
                assertStudiesMatch(
                        response,
                        List.of(LINE_INTERVIEW, TOEIC, RABBITMQ, REDIS, OOP, JSP),
                        List.of(4, 4, 4, 5, 5, 6),
                        List.of(
                                List.of(members.get(0), members.get(1), members.get(2), members.get(3), members.get(4)),
                                List.of(members.get(1), members.get(2), members.get(4)),
                                List.of(members.get(0), members.get(2), members.get(4)),
                                List.of(members.get(1), members.get(3)),
                                List.of(),
                                List.of()
                        ),
                        false
                );
            }
        }
    }

    private void assertStudiesMatch(
            final ValidatableResponse response,
            final List<StudyFixture> studyFixtures,
            final List<Integer> participantMembers,
            final List<List<Long>> likeMarkingMemberIds,
            final boolean hasNext
    ) {
        final int totalCount = studyFixtures.size();
        response
                .body("studies", hasSize(totalCount))
                .body("hasNext", is(hasNext));

        for (int i = 0; i < totalCount; i++) {
            final String index = String.format("studies[%d]", i);
            final StudyFixture study = studyFixtures.get(i);
            final List<Long> indexOfLikeMarkingMemberIds = likeMarkingMemberIds.get(i);

            response
                    .body(index + ".id", is(studies.get(study).intValue()))
                    .body(index + ".name", is(study.getName().getValue()))
                    .body(index + ".description", is(study.getDescription().getValue()))
                    .body(index + ".category", is(study.getCategory().getName()))
                    .body(index + ".thumbnail.name", is(study.getThumbnail().getImageName()))
                    .body(index + ".thumbnail.background", is(study.getThumbnail().getBackground()))
                    .body(index + ".type", is(study.getType().name()))
                    .body(index + ".maxMember", is(study.getCapacity().getValue()))
                    .body(index + ".participantMembers", is(participantMembers.get(i)))
                    .body(index + ".likeMarkingMembers", containsInAnyOrder(
                            indexOfLikeMarkingMemberIds
                                    .stream()
                                    .map(Long::intValue)
                                    .toArray()
                    ));
        }
    }
}
