package com.kgu.studywithme.study.infrastructure.query;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.favorite.domain.Favorite;
import com.kgu.studywithme.favorite.infrastructure.persistence.FavoriteJpaRepository;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.study.infrastructure.query.dto.StudyPreview;
import com.kgu.studywithme.study.utils.QueryStudyByCategoryCondition;
import com.kgu.studywithme.study.utils.QueryStudyByRecommendCondition;
import com.kgu.studywithme.studyreview.domain.StudyReview;
import com.kgu.studywithme.studyreview.domain.StudyReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.kgu.studywithme.category.domain.Category.INTERVIEW;
import static com.kgu.studywithme.category.domain.Category.PROGRAMMING;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY1;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY2;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY3;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY4;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY5;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY6;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY7;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY8;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY9;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.ARABIC;
import static com.kgu.studywithme.common.fixture.StudyFixture.AWS;
import static com.kgu.studywithme.common.fixture.StudyFixture.CHINESE;
import static com.kgu.studywithme.common.fixture.StudyFixture.DOCKER;
import static com.kgu.studywithme.common.fixture.StudyFixture.EFFECTIVE_JAVA;
import static com.kgu.studywithme.common.fixture.StudyFixture.FRENCH;
import static com.kgu.studywithme.common.fixture.StudyFixture.GERMAN;
import static com.kgu.studywithme.common.fixture.StudyFixture.GOOGLE_INTERVIEW;
import static com.kgu.studywithme.common.fixture.StudyFixture.JAPANESE;
import static com.kgu.studywithme.common.fixture.StudyFixture.JPA;
import static com.kgu.studywithme.common.fixture.StudyFixture.KAKAO_INTERVIEW;
import static com.kgu.studywithme.common.fixture.StudyFixture.KOTLIN;
import static com.kgu.studywithme.common.fixture.StudyFixture.KUBERNETES;
import static com.kgu.studywithme.common.fixture.StudyFixture.LINE_INTERVIEW;
import static com.kgu.studywithme.common.fixture.StudyFixture.NAVER_INTERVIEW;
import static com.kgu.studywithme.common.fixture.StudyFixture.NETWORK;
import static com.kgu.studywithme.common.fixture.StudyFixture.OS;
import static com.kgu.studywithme.common.fixture.StudyFixture.PYTHON;
import static com.kgu.studywithme.common.fixture.StudyFixture.REAL_MYSQL;
import static com.kgu.studywithme.common.fixture.StudyFixture.RUST;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.common.fixture.StudyFixture.TOEFL;
import static com.kgu.studywithme.common.fixture.StudyFixture.TOEIC;
import static com.kgu.studywithme.common.fixture.StudyFixture.TOSS_INTERVIEW;
import static com.kgu.studywithme.study.domain.StudyType.OFFLINE;
import static com.kgu.studywithme.study.domain.StudyType.ONLINE;
import static com.kgu.studywithme.study.utils.PagingConstants.SortType.DATE;
import static com.kgu.studywithme.study.utils.PagingConstants.SortType.FAVORITE;
import static com.kgu.studywithme.study.utils.PagingConstants.SortType.REVIEW;
import static com.kgu.studywithme.study.utils.PagingConstants.getDefaultPageRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study -> StudyCategoryQueryRepository 테스트")
class StudyCategoryQueryRepositoryTest extends RepositoryTest {
    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StudyReviewRepository studyReviewRepository;

    @Autowired
    private FavoriteJpaRepository favoriteJpaRepository;

    private static final Pageable PAGE_REQUEST_1 = getDefaultPageRequest(0);
    private static final Pageable PAGE_REQUEST_2 = getDefaultPageRequest(1);
    private static final Pageable PAGE_REQUEST_3 = getDefaultPageRequest(2);
    private static final LocalDateTime NOW = LocalDateTime.now();

    private Member host;
    private final Member[] member = new Member[9];
    private final Study[] language = new Study[7];
    private final Study[] interview = new Study[5];
    private final Study[] programming = new Study[12];
    private final List<Favorite> favorites = new ArrayList<>();
    private final List<StudyReview> reviews = new ArrayList<>();

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());

        member[0] = memberRepository.save(DUMMY1.toMember());
        member[1] = memberRepository.save(DUMMY2.toMember());
        member[2] = memberRepository.save(DUMMY3.toMember());
        member[3] = memberRepository.save(DUMMY4.toMember());
        member[4] = memberRepository.save(DUMMY5.toMember());
        member[5] = memberRepository.save(DUMMY6.toMember());
        member[6] = memberRepository.save(DUMMY7.toMember());
        member[7] = memberRepository.save(DUMMY8.toMember());
        member[8] = memberRepository.save(DUMMY9.toMember());

        language[0] = TOEIC.toOnlineStudy(host.getId());
        language[1] = TOEFL.toOnlineStudy(host.getId());
        language[2] = JAPANESE.toOnlineStudy(host.getId());
        language[3] = CHINESE.toOnlineStudy(host.getId());
        language[4] = FRENCH.toOnlineStudy(host.getId());
        language[5] = GERMAN.toOnlineStudy(host.getId());
        language[6] = ARABIC.toOnlineStudy(host.getId());

        interview[0] = TOSS_INTERVIEW.toOfflineStudy(host.getId());
        interview[1] = KAKAO_INTERVIEW.toOfflineStudy(host.getId());
        interview[2] = NAVER_INTERVIEW.toOfflineStudy(host.getId());
        interview[3] = LINE_INTERVIEW.toOfflineStudy(host.getId());
        interview[4] = GOOGLE_INTERVIEW.toOfflineStudy(host.getId());

        programming[0] = SPRING.toOnlineStudy(host.getId());
        programming[1] = JPA.toOnlineStudy(host.getId());
        programming[2] = REAL_MYSQL.toOfflineStudy(host.getId());
        programming[3] = KOTLIN.toOnlineStudy(host.getId());
        programming[4] = NETWORK.toOnlineStudy(host.getId());
        programming[5] = EFFECTIVE_JAVA.toOnlineStudy(host.getId());
        programming[6] = AWS.toOfflineStudy(host.getId());
        programming[7] = DOCKER.toOnlineStudy(host.getId());
        programming[8] = KUBERNETES.toOnlineStudy(host.getId());
        programming[9] = PYTHON.toOnlineStudy(host.getId());
        programming[10] = RUST.toOnlineStudy(host.getId());
        programming[11] = OS.toOnlineStudy(host.getId());
    }

    @Nested
    @DisplayName("각 카테고리 별 스터디 리스트 조회")
    class FetchStudyByCategory {
        @Test
        @DisplayName("최신순으로 프로그래밍 스터디 리스트를 조회한다")
        void date() {
            // given
            initDataWithRegisterDate();

            /* 온라인 스터디 */
            final QueryStudyByCategoryCondition onlineCondition = new QueryStudyByCategoryCondition(
                    PROGRAMMING,
                    DATE,
                    ONLINE.getValue(),
                    null,
                    null
            );
            final Slice<StudyPreview> result1 = studyRepository.fetchStudyByCategory(onlineCondition, PAGE_REQUEST_1);
            assertThat(result1.hasNext()).isTrue();
            assertThatStudiesMatch(
                    result1.getContent(),
                    List.of(
                            programming[11], programming[10], programming[9], programming[8],
                            programming[7], programming[5], programming[4], programming[3]
                    ),
                    List.of(
                            List.of(), List.of(), List.of(), List.of(),
                            List.of(), List.of(), List.of(), List.of()
                    )
            );

            final Slice<StudyPreview> result2 = studyRepository.fetchStudyByCategory(onlineCondition, PAGE_REQUEST_2);
            assertThat(result2.hasNext()).isFalse();
            assertThatStudiesMatch(
                    result2.getContent(),
                    List.of(programming[1], programming[0]),
                    List.of(List.of(), List.of())
            );

            /* 오프라인 스터디 */
            final QueryStudyByCategoryCondition offlineCondition = new QueryStudyByCategoryCondition(
                    PROGRAMMING,
                    DATE,
                    OFFLINE.getValue(),
                    null,
                    null
            );
            final Slice<StudyPreview> result3 = studyRepository.fetchStudyByCategory(offlineCondition, PAGE_REQUEST_1);
            assertThat(result3.hasNext()).isFalse();
            assertThatStudiesMatch(
                    result3.getContent(),
                    List.of(programming[6], programming[2]),
                    List.of(List.of(), List.of())
            );

            /* 온라인 + 오프라인 통합 */
            final QueryStudyByCategoryCondition totalCondition = new QueryStudyByCategoryCondition(
                    PROGRAMMING,
                    DATE,
                    null,
                    null,
                    null
            );
            final Slice<StudyPreview> result4 = studyRepository.fetchStudyByCategory(totalCondition, PAGE_REQUEST_1);
            assertThat(result4.hasNext()).isTrue();
            assertThatStudiesMatch(
                    result4.getContent(),
                    List.of(
                            programming[11], programming[10], programming[9], programming[8],
                            programming[7], programming[6], programming[5], programming[4]
                    ),
                    List.of(
                            List.of(), List.of(), List.of(), List.of(),
                            List.of(), List.of(), List.of(), List.of()
                    )
            );

            final Slice<StudyPreview> result5 = studyRepository.fetchStudyByCategory(totalCondition, PAGE_REQUEST_2);
            assertThat(result5.hasNext()).isFalse();
            assertThatStudiesMatch(
                    result5.getContent(),
                    List.of(programming[3], programming[2], programming[1], programming[0]),
                    List.of(List.of(), List.of(), List.of(), List.of())
            );
        }

        @Test
        @DisplayName("최신순 + 오프라인 지역으로 면접 스터디 리스트를 조회한다")
        void dateWithAddress() {
            // given
            initDataWithRegisterDate();
            final QueryStudyByCategoryCondition condition1 = new QueryStudyByCategoryCondition(
                    INTERVIEW,
                    DATE,
                    OFFLINE.getValue(),
                    null,
                    "성남시"
            );
            final QueryStudyByCategoryCondition condition2 = new QueryStudyByCategoryCondition(
                    INTERVIEW,
                    DATE,
                    OFFLINE.getValue(),
                    "경기도",
                    "성남시"
            );
            final QueryStudyByCategoryCondition condition3 = new QueryStudyByCategoryCondition(
                    INTERVIEW,
                    DATE,
                    OFFLINE.getValue(),
                    "경기도",
                    null
            );

            // 서울 특별시 & 강남구
            final Slice<StudyPreview> result1 = studyRepository.fetchStudyByCategory(condition1, PAGE_REQUEST_1);
            final Slice<StudyPreview> result2 = studyRepository.fetchStudyByCategory(condition2, PAGE_REQUEST_1);
            final Slice<StudyPreview> result3 = studyRepository.fetchStudyByCategory(condition3, PAGE_REQUEST_1);
            assertThat(result1.hasNext()).isFalse();
            assertThat(result2.hasNext()).isFalse();
            assertThat(result3.hasNext()).isFalse();

            assertThatStudiesMatch(
                    result1.getContent(),
                    List.of(interview[3], interview[2], interview[1]),
                    List.of(List.of(), List.of(), List.of())
            );
            assertThatStudiesMatch(
                    result2.getContent(),
                    List.of(interview[3], interview[2], interview[1]),
                    List.of(List.of(), List.of(), List.of())
            );
            assertThatStudiesMatch(
                    result3.getContent(),
                    List.of(interview[3], interview[2], interview[1]),
                    List.of(List.of(), List.of(), List.of())
            );
        }

        @Test
        @DisplayName("찜이 많은 순으로 프로그래밍 스터디 리스트를 조회한다")
        void favorite() {
            // given
            initDataWithFavorite();

            /* 온라인 스터디 */
            final QueryStudyByCategoryCondition onlineCondition = new QueryStudyByCategoryCondition(
                    PROGRAMMING,
                    FAVORITE,
                    ONLINE.getValue(),
                    null,
                    null
            );
            final Slice<StudyPreview> result1 = studyRepository.fetchStudyByCategory(onlineCondition, PAGE_REQUEST_1);
            assertThat(result1.hasNext()).isTrue();
            assertThatStudiesMatch(
                    result1.getContent(),
                    List.of(
                            programming[9], programming[3], programming[5], programming[8],
                            programming[7], programming[0], programming[11], programming[10]
                    ),
                    List.of(
                            List.of(
                                    member[0], member[1], member[2], member[3],
                                    member[4], member[5], member[6], member[7],
                                    member[8]
                            ),
                            List.of(
                                    member[0], member[1], member[2], member[3],
                                    member[4], member[5], member[6], member[7]
                            ),
                            List.of(
                                    member[0], member[1], member[2], member[3],
                                    member[4], member[5], member[6]
                            ),
                            List.of(
                                    member[0], member[1], member[2], member[3],
                                    member[4], member[5]
                            ),
                            List.of(
                                    member[0], member[1], member[2], member[3],
                                    member[4]
                            ),
                            List.of(
                                    member[0], member[1], member[2], member[3],
                                    member[4]
                            ),
                            List.of(member[0], member[1], member[2], member[3]),
                            List.of(member[0], member[1], member[2], member[3])
                    )
            );

            final Slice<StudyPreview> result2 = studyRepository.fetchStudyByCategory(onlineCondition, PAGE_REQUEST_2);
            assertThat(result2.hasNext()).isFalse();
            assertThatStudiesMatch(
                    result2.getContent(),
                    List.of(programming[1], programming[4]),
                    List.of(
                            List.of(member[0], member[1], member[2]),
                            List.of(member[0])
                    )
            );

            /* 오프라인 스터디 */
            final QueryStudyByCategoryCondition offlineCondition = new QueryStudyByCategoryCondition(
                    PROGRAMMING,
                    FAVORITE,
                    OFFLINE.getValue(),
                    null,
                    null
            );
            final Slice<StudyPreview> result3 = studyRepository.fetchStudyByCategory(offlineCondition, PAGE_REQUEST_1);
            assertThat(result3.hasNext()).isFalse();
            assertThatStudiesMatch(
                    result3.getContent(),
                    List.of(programming[2], programming[6]),
                    List.of(
                            List.of(
                                    member[0], member[1], member[2], member[3],
                                    member[4], member[5], member[6], member[7]
                            ),
                            List.of(
                                    member[0], member[1], member[2], member[3],
                                    member[4], member[5], member[6]
                            )
                    )
            );

            /* 온라인 + 오프라인 통합 */
            final QueryStudyByCategoryCondition totalCondition = new QueryStudyByCategoryCondition(
                    PROGRAMMING,
                    FAVORITE,
                    null,
                    null,
                    null
            );
            final Slice<StudyPreview> result4 = studyRepository.fetchStudyByCategory(totalCondition, PAGE_REQUEST_1);
            assertThat(result4.hasNext()).isTrue();
            assertThatStudiesMatch(
                    result4.getContent(),
                    List.of(
                            programming[9], programming[3], programming[2], programming[6],
                            programming[5], programming[8], programming[7], programming[0]
                    ),
                    List.of(
                            List.of(
                                    member[0], member[1], member[2], member[3],
                                    member[4], member[5], member[6], member[7],
                                    member[8]
                            ),
                            List.of(
                                    member[0], member[1], member[2], member[3],
                                    member[4], member[5], member[6], member[7]
                            ),
                            List.of(
                                    member[0], member[1], member[2], member[3],
                                    member[4], member[5], member[6], member[7]
                            ),
                            List.of(
                                    member[0], member[1], member[2], member[3],
                                    member[4], member[5], member[6]
                            ),
                            List.of(
                                    member[0], member[1], member[2], member[3],
                                    member[4], member[5], member[6]
                            ),
                            List.of(
                                    member[0], member[1], member[2], member[3],
                                    member[4], member[5]
                            ),
                            List.of(
                                    member[0], member[1], member[2], member[3],
                                    member[4]
                            ),
                            List.of(
                                    member[0], member[1], member[2], member[3],
                                    member[4]
                            )
                    )
            );

            final Slice<StudyPreview> result5 = studyRepository.fetchStudyByCategory(totalCondition, PAGE_REQUEST_2);
            assertThat(result5.hasNext()).isFalse();
            assertThatStudiesMatch(
                    result5.getContent(),
                    List.of(programming[11], programming[10], programming[1], programming[4]),
                    List.of(
                            List.of(member[0], member[1], member[2], member[3]),
                            List.of(member[0], member[1], member[2], member[3]),
                            List.of(member[0], member[1], member[2]),
                            List.of(member[0])
                    )
            );
        }

        @Test
        @DisplayName("리뷰가 많은 순으로 프로그래밍 스터디 리스트를 조회한다")
        void review() {
            // given
            initDataWithReviews();

            /* 온라인 스터디 */
            final QueryStudyByCategoryCondition onlineCondition = new QueryStudyByCategoryCondition(
                    PROGRAMMING,
                    REVIEW,
                    ONLINE.getValue(),
                    null,
                    null
            );
            final Slice<StudyPreview> result1 = studyRepository.fetchStudyByCategory(onlineCondition, PAGE_REQUEST_1);
            assertThat(result1.hasNext()).isTrue();
            assertThatStudiesMatch(
                    result1.getContent(),
                    List.of(
                            programming[9], programming[3], programming[5], programming[8],
                            programming[7], programming[0], programming[11], programming[10]
                    ),
                    List.of(
                            List.of(), List.of(), List.of(), List.of(),
                            List.of(), List.of(), List.of(), List.of()
                    )
            );

            final Slice<StudyPreview> result2 = studyRepository.fetchStudyByCategory(onlineCondition, PAGE_REQUEST_2);
            assertThat(result2.hasNext()).isFalse();
            assertThatStudiesMatch(
                    result2.getContent(),
                    List.of(programming[1], programming[4]),
                    List.of(List.of(), List.of())
            );

            /* 오프라인 스터디 */
            final QueryStudyByCategoryCondition offlineCondition = new QueryStudyByCategoryCondition(
                    PROGRAMMING,
                    REVIEW,
                    OFFLINE.getValue(),
                    null,
                    null
            );
            final Slice<StudyPreview> result3 = studyRepository.fetchStudyByCategory(offlineCondition, PAGE_REQUEST_1);
            assertThat(result3.hasNext()).isFalse();
            assertThatStudiesMatch(
                    result3.getContent(),
                    List.of(programming[2], programming[6]),
                    List.of(List.of(), List.of())
            );

            /* 온라인 + 오프라인 통합 */
            final QueryStudyByCategoryCondition totalCondition = new QueryStudyByCategoryCondition(
                    PROGRAMMING,
                    REVIEW,
                    null,
                    null,
                    null
            );
            final Slice<StudyPreview> result4 = studyRepository.fetchStudyByCategory(totalCondition, PAGE_REQUEST_1);
            assertThat(result4.hasNext()).isTrue();
            assertThatStudiesMatch(
                    result4.getContent(),
                    List.of(
                            programming[9], programming[3], programming[2], programming[6],
                            programming[5], programming[8], programming[7], programming[0]
                    ),
                    List.of(
                            List.of(), List.of(), List.of(), List.of(),
                            List.of(), List.of(), List.of(), List.of()
                    )
            );

            final Slice<StudyPreview> result5 = studyRepository.fetchStudyByCategory(totalCondition, PAGE_REQUEST_2);
            assertThat(result5.hasNext()).isFalse();
            assertThatStudiesMatch(
                    result5.getContent(),
                    List.of(programming[11], programming[10], programming[1], programming[4]),
                    List.of(List.of(), List.of(), List.of(), List.of())
            );
        }
    }

    @Nested
    @DisplayName("사용자의 관심사에 따른 스터디 리스트 조회 [Ex) 언어, 인터뷰, 프로그래밍]")
    class FetchStudyByRecommend {
        @Test
        @DisplayName("최신순으로 스터디 리스트를 조회한다")
        void date() {
            // given
            initDataWithRegisterDate();

            /* 온라인 스터디 */
            final QueryStudyByRecommendCondition onlineCondition = new QueryStudyByRecommendCondition(
                    host.getId(),
                    DATE,
                    ONLINE.getValue(),
                    null,
                    null
            );
            final Slice<StudyPreview> result1 = studyRepository.fetchStudyByRecommend(onlineCondition, PAGE_REQUEST_1);
            assertThat(result1.hasNext()).isTrue();
            assertThatStudiesMatch(
                    result1.getContent(),
                    List.of(
                            programming[11], programming[10], programming[9], programming[8],
                            programming[7], programming[5], programming[4], programming[3]
                    ),
                    List.of(
                            List.of(), List.of(), List.of(), List.of(),
                            List.of(), List.of(), List.of(), List.of()
                    )
            );

            final Slice<StudyPreview> result2 = studyRepository.fetchStudyByRecommend(onlineCondition, PAGE_REQUEST_2);
            assertThat(result2.hasNext()).isTrue();
            assertThatStudiesMatch(
                    result2.getContent(),
                    List.of(
                            programming[1], programming[0], language[6], language[5],
                            language[4], language[3], language[2], language[1]
                    ),
                    List.of(
                            List.of(), List.of(), List.of(), List.of(),
                            List.of(), List.of(), List.of(), List.of()
                    )
            );

            final Slice<StudyPreview> result3 = studyRepository.fetchStudyByRecommend(onlineCondition, PAGE_REQUEST_3);
            assertThat(result3.hasNext()).isFalse();
            assertThatStudiesMatch(
                    result3.getContent(),
                    List.of(language[0]),
                    List.of(List.of())
            );

            /* 오프라인 스터디 */
            final QueryStudyByRecommendCondition offlineCondition = new QueryStudyByRecommendCondition(
                    host.getId(),
                    DATE,
                    OFFLINE.getValue(),
                    null,
                    null
            );
            final Slice<StudyPreview> result4 = studyRepository.fetchStudyByRecommend(offlineCondition, PAGE_REQUEST_1);
            assertThat(result4.hasNext()).isFalse();
            assertThatStudiesMatch(
                    result4.getContent(),
                    List.of(
                            programming[6], programming[2], interview[4], interview[3],
                            interview[2], interview[1], interview[0]
                    ),
                    List.of(
                            List.of(), List.of(), List.of(), List.of(),
                            List.of(), List.of(), List.of()
                    )
            );

            /* 온라인 + 오프라인 통합 */
            final QueryStudyByRecommendCondition totalCondition = new QueryStudyByRecommendCondition(
                    host.getId(),
                    DATE,
                    null,
                    null,
                    null
            );
            final Slice<StudyPreview> result5 = studyRepository.fetchStudyByRecommend(totalCondition, PAGE_REQUEST_1);
            assertThat(result5.hasNext()).isTrue();
            assertThatStudiesMatch(
                    result5.getContent(),
                    List.of(
                            programming[11], programming[10], programming[9], programming[8],
                            programming[7], programming[6], programming[5], programming[4]
                    ),
                    List.of(
                            List.of(), List.of(), List.of(), List.of(),
                            List.of(), List.of(), List.of(), List.of()
                    )
            );

            final Slice<StudyPreview> result6 = studyRepository.fetchStudyByRecommend(totalCondition, PAGE_REQUEST_2);
            assertThat(result6.hasNext()).isTrue();
            assertThatStudiesMatch(
                    result6.getContent(),
                    List.of(
                            programming[3], programming[2], programming[1], programming[0],
                            interview[4], interview[3], interview[2], interview[1]
                    ),
                    List.of(
                            List.of(), List.of(), List.of(), List.of(),
                            List.of(), List.of(), List.of(), List.of()
                    )
            );

            final Slice<StudyPreview> result7 = studyRepository.fetchStudyByRecommend(totalCondition, PAGE_REQUEST_3);
            assertThat(result7.hasNext()).isFalse();
            assertThatStudiesMatch(
                    result7.getContent(),
                    List.of(
                            interview[0], language[6], language[5], language[4],
                            language[3], language[2], language[1], language[0]
                    ),
                    List.of(
                            List.of(), List.of(), List.of(), List.of(),
                            List.of(), List.of(), List.of(), List.of()
                    )
            );
        }

        @Test
        @DisplayName("최신순 + 오프라인 지역으로 스터디 리스트를 조회한다")
        void dateWithAddress() {
            // given
            initDataWithRegisterDate();
            final QueryStudyByRecommendCondition condition1 = new QueryStudyByRecommendCondition(
                    host.getId(),
                    DATE,
                    OFFLINE.getValue(),
                    "서울특별시",
                    "강남구"
            );
            final QueryStudyByRecommendCondition condition2 = new QueryStudyByRecommendCondition(
                    host.getId(),
                    DATE,
                    OFFLINE.getValue(),
                    null,
                    "강남구"
            );
            final QueryStudyByRecommendCondition condition3 = new QueryStudyByRecommendCondition(
                    host.getId(),
                    DATE,
                    OFFLINE.getValue(),
                    "서울특별시",
                    null
            );

            // 서울 특별시 & 강남구
            final Slice<StudyPreview> result1 = studyRepository.fetchStudyByRecommend(condition1, PAGE_REQUEST_1);
            final Slice<StudyPreview> result2 = studyRepository.fetchStudyByRecommend(condition2, PAGE_REQUEST_1);
            final Slice<StudyPreview> result3 = studyRepository.fetchStudyByRecommend(condition3, PAGE_REQUEST_1);
            assertThat(result1.hasNext()).isFalse();
            assertThat(result2.hasNext()).isFalse();
            assertThat(result3.hasNext()).isFalse();

            assertThatStudiesMatch(
                    result1.getContent(),
                    List.of(programming[6], programming[2], interview[4], interview[0]),
                    List.of(List.of(), List.of(), List.of(), List.of())
            );
            assertThatStudiesMatch(
                    result2.getContent(),
                    List.of(programming[6], programming[2], interview[4], interview[0]),
                    List.of(List.of(), List.of(), List.of(), List.of())
            );
            assertThatStudiesMatch(
                    result3.getContent(),
                    List.of(programming[6], programming[2], interview[4], interview[0]),
                    List.of(List.of(), List.of(), List.of(), List.of())
            );
        }

        @Test
        @DisplayName("찜이 많은 순으로 스터디 리스트를 조회한다")
        void favorite() {
            // given
            initDataWithFavorite();

            /* 온라인 스터디 */
            final QueryStudyByRecommendCondition onlineCondition = new QueryStudyByRecommendCondition(
                    host.getId(),
                    FAVORITE,
                    ONLINE.getValue(),
                    null,
                    null
            );
            final Slice<StudyPreview> result1 = studyRepository.fetchStudyByRecommend(onlineCondition, PAGE_REQUEST_1);
            assertThat(result1.hasNext()).isTrue();
            assertThatStudiesMatch(
                    result1.getContent(),
                    List.of(
                            programming[9], programming[3], programming[5], language[0],
                            programming[8], programming[7], programming[0], language[5]
                    ),
                    List.of(
                            List.of(
                                    member[0], member[1], member[2], member[3],
                                    member[4], member[5], member[6], member[7], member[8]
                            ),
                            List.of(
                                    member[0], member[1], member[2], member[3],
                                    member[4], member[5], member[6], member[7]
                            ),
                            List.of(
                                    member[0], member[1], member[2], member[3],
                                    member[4], member[5], member[6]
                            ),
                            List.of(
                                    member[0], member[1], member[2], member[3],
                                    member[4], member[5], member[6]
                            ),
                            List.of(
                                    member[0], member[1], member[2], member[3],
                                    member[4], member[5]
                            ),
                            List.of(
                                    member[0], member[1], member[2], member[3],
                                    member[4]
                            ),
                            List.of(
                                    member[0], member[1], member[2], member[3],
                                    member[4]
                            ),
                            List.of(
                                    member[0], member[1], member[2], member[3],
                                    member[4]
                            )
                    )
            );

            final Slice<StudyPreview> result2 = studyRepository.fetchStudyByRecommend(onlineCondition, PAGE_REQUEST_2);
            assertThat(result2.hasNext()).isTrue();
            assertThatStudiesMatch(
                    result2.getContent(),
                    List.of(
                            programming[11], programming[10], language[3], programming[1],
                            language[6], language[2], language[1], programming[4]
                    ),
                    List.of(
                            List.of(member[0], member[1], member[2], member[3]),
                            List.of(member[0], member[1], member[2], member[3]),
                            List.of(member[0], member[1], member[2], member[3]),
                            List.of(member[0], member[1], member[2]),
                            List.of(member[0], member[1], member[2]),
                            List.of(member[0], member[1], member[2]),
                            List.of(member[0], member[1], member[2]),
                            List.of(member[0])
                    )
            );

            final Slice<StudyPreview> result3 = studyRepository.fetchStudyByRecommend(onlineCondition, PAGE_REQUEST_3);
            assertThat(result3.hasNext()).isFalse();
            assertThatStudiesMatch(
                    result3.getContent(),
                    List.of(language[4]),
                    List.of(List.of())
            );

            /* 오프라인 스터디 */
            final QueryStudyByRecommendCondition offlineCondition = new QueryStudyByRecommendCondition(
                    host.getId(),
                    FAVORITE,
                    OFFLINE.getValue(),
                    null,
                    null
            );
            final Slice<StudyPreview> result4 = studyRepository.fetchStudyByRecommend(offlineCondition, PAGE_REQUEST_1);
            assertThat(result4.hasNext()).isFalse();
            assertThatStudiesMatch(
                    result4.getContent(),
                    List.of(
                            programming[2], programming[6], interview[3], interview[4],
                            interview[1], interview[2], interview[0]
                    ),
                    List.of(
                            List.of(
                                    member[0], member[1], member[2], member[3],
                                    member[4], member[5], member[6], member[7]
                            ),
                            List.of(
                                    member[0], member[1], member[2], member[3],
                                    member[4], member[5], member[6]
                            ),
                            List.of(
                                    member[0], member[1], member[2], member[3],
                                    member[4], member[5], member[6]
                            ),
                            List.of(member[0], member[1], member[2], member[3]),
                            List.of(member[0], member[1], member[2], member[3]),
                            List.of(member[0], member[1]),
                            List.of(member[0])
                    )
            );

            /* 온라인 + 오프라인 통합 */
            final QueryStudyByRecommendCondition totalCondition = new QueryStudyByRecommendCondition(
                    host.getId(),
                    FAVORITE,
                    null,
                    null,
                    null
            );
            final Slice<StudyPreview> result5 = studyRepository.fetchStudyByRecommend(totalCondition, PAGE_REQUEST_1);
            assertThat(result5.hasNext()).isTrue();
            assertThatStudiesMatch(
                    result5.getContent(),
                    List.of(
                            programming[9], programming[3], programming[2], programming[6],
                            programming[5], interview[3], language[0], programming[8]
                    ),
                    List.of(
                            List.of(
                                    member[0], member[1], member[2], member[3],
                                    member[4], member[5], member[6], member[7], member[8]
                            ),
                            List.of(
                                    member[0], member[1], member[2], member[3],
                                    member[4], member[5], member[6], member[7]
                            ),
                            List.of(
                                    member[0], member[1], member[2], member[3],
                                    member[4], member[5], member[6], member[7]
                            ),
                            List.of(
                                    member[0], member[1], member[2], member[3],
                                    member[4], member[5], member[6]
                            ),
                            List.of(
                                    member[0], member[1], member[2], member[3],
                                    member[4], member[5], member[6]
                            ),
                            List.of(
                                    member[0], member[1], member[2], member[3],
                                    member[4], member[5], member[6]
                            ),
                            List.of(
                                    member[0], member[1], member[2], member[3],
                                    member[4], member[5], member[6]
                            ),
                            List.of(
                                    member[0], member[1], member[2], member[3],
                                    member[4], member[5]
                            )
                    )
            );

            final Slice<StudyPreview> result6 = studyRepository.fetchStudyByRecommend(totalCondition, PAGE_REQUEST_2);
            assertThat(result6.hasNext()).isTrue();
            assertThatStudiesMatch(
                    result6.getContent(),
                    List.of(
                            programming[7], programming[0], language[5], programming[11],
                            programming[10], interview[4], interview[1], language[3]
                    ),
                    List.of(
                            List.of(
                                    member[0], member[1], member[2], member[3],
                                    member[4]
                            ),
                            List.of(
                                    member[0], member[1], member[2], member[3],
                                    member[4]
                            ),
                            List.of(
                                    member[0], member[1], member[2], member[3],
                                    member[4]
                            ),
                            List.of(member[0], member[1], member[2], member[3]),
                            List.of(member[0], member[1], member[2], member[3]),
                            List.of(member[0], member[1], member[2], member[3]),
                            List.of(member[0], member[1], member[2], member[3]),
                            List.of(member[0], member[1], member[2], member[3])
                    )
            );

            final Slice<StudyPreview> result7 = studyRepository.fetchStudyByRecommend(totalCondition, PAGE_REQUEST_3);
            assertThat(result7.hasNext()).isFalse();
            assertThatStudiesMatch(
                    result7.getContent(),
                    List.of(
                            programming[1], language[6], language[2], language[1],
                            interview[2], programming[4], interview[0], language[4]
                    ),
                    List.of(
                            List.of(member[0], member[1], member[2]),
                            List.of(member[0], member[1], member[2]),
                            List.of(member[0], member[1], member[2]),
                            List.of(member[0], member[1], member[2]),
                            List.of(member[0], member[1]),
                            List.of(member[0]),
                            List.of(member[0]),
                            List.of()
                    )
            );
        }

        @Test
        @DisplayName("리뷰가 많은 순으로 스터디 리스트를 조회한다")
        void review() {
            // given
            initDataWithReviews();

            /* 온라인 스터디 */
            final QueryStudyByRecommendCondition onlineCondition = new QueryStudyByRecommendCondition(
                    host.getId(),
                    REVIEW,
                    ONLINE.getValue(),
                    null,
                    null
            );
            final Slice<StudyPreview> result1 = studyRepository.fetchStudyByRecommend(onlineCondition, PAGE_REQUEST_1);
            assertThat(result1.hasNext()).isTrue();
            assertThatStudiesMatch(
                    result1.getContent(),
                    List.of(
                            programming[9], programming[3], programming[5], language[0],
                            programming[8], programming[7], programming[0], language[5]
                    ),
                    List.of(
                            List.of(), List.of(), List.of(), List.of(),
                            List.of(), List.of(), List.of(), List.of()
                    )
            );

            final Slice<StudyPreview> result2 = studyRepository.fetchStudyByRecommend(onlineCondition, PAGE_REQUEST_2);
            assertThat(result2.hasNext()).isTrue();
            assertThatStudiesMatch(
                    result2.getContent(),
                    List.of(
                            programming[11], programming[10], language[3], programming[1],
                            language[6], language[2], language[1], programming[4]
                    ),
                    List.of(
                            List.of(), List.of(), List.of(), List.of(),
                            List.of(), List.of(), List.of(), List.of()
                    )
            );

            final Slice<StudyPreview> result3 = studyRepository.fetchStudyByRecommend(onlineCondition, PAGE_REQUEST_3);
            assertThat(result3.hasNext()).isFalse();
            assertThatStudiesMatch(
                    result3.getContent(),
                    List.of(language[4]),
                    List.of(List.of())
            );

            /* 오프라인 스터디 */
            final QueryStudyByRecommendCondition offlineCondition = new QueryStudyByRecommendCondition(
                    host.getId(),
                    REVIEW,
                    OFFLINE.getValue(),
                    null,
                    null
            );
            final Slice<StudyPreview> result4 = studyRepository.fetchStudyByRecommend(offlineCondition, PAGE_REQUEST_1);
            assertThat(result4.hasNext()).isFalse();
            assertThatStudiesMatch(
                    result4.getContent(),
                    List.of(
                            programming[2], programming[6], interview[3], interview[4],
                            interview[1], interview[2], interview[0]
                    ),
                    List.of(
                            List.of(), List.of(), List.of(), List.of(),
                            List.of(), List.of(), List.of()
                    )
            );

            /* 온라인 + 오프라인 통합 */
            final QueryStudyByRecommendCondition totalCondition = new QueryStudyByRecommendCondition(
                    host.getId(),
                    REVIEW,
                    null,
                    null,
                    null
            );
            final Slice<StudyPreview> result5 = studyRepository.fetchStudyByRecommend(totalCondition, PAGE_REQUEST_1);
            assertThat(result5.hasNext()).isTrue();
            assertThatStudiesMatch(
                    result5.getContent(),
                    List.of(
                            programming[9], programming[3], programming[2], programming[6],
                            programming[5], interview[3], language[0], programming[8]
                    ),
                    List.of(
                            List.of(), List.of(), List.of(), List.of(),
                            List.of(), List.of(), List.of(), List.of()
                    )
            );

            final Slice<StudyPreview> result6 = studyRepository.fetchStudyByRecommend(totalCondition, PAGE_REQUEST_2);
            assertThat(result6.hasNext()).isTrue();
            assertThatStudiesMatch(
                    result6.getContent(),
                    List.of(
                            programming[7], programming[0], language[5], programming[11],
                            programming[10], interview[4], interview[1], language[3]
                    ),
                    List.of(
                            List.of(), List.of(), List.of(), List.of(),
                            List.of(), List.of(), List.of(), List.of()
                    )
            );

            final Slice<StudyPreview> result7 = studyRepository.fetchStudyByRecommend(totalCondition, PAGE_REQUEST_3);
            assertThat(result7.hasNext()).isFalse();
            assertThatStudiesMatch(
                    result7.getContent(),
                    List.of(
                            programming[1], language[6], language[2], language[1],
                            interview[2], programming[4], interview[0], language[4]
                    ),
                    List.of(
                            List.of(), List.of(), List.of(), List.of(),
                            List.of(), List.of(), List.of(), List.of()
                    )
            );
        }
    }

    private void initDataWithRegisterDate() {
        final List<Study> buffer = new LinkedList<>();
        int day = language.length + interview.length + programming.length;

        for (Study study : language) {
            ReflectionTestUtils.setField(study, "createdAt", NOW.minusDays(day--));
            buffer.add(study);
        }

        for (Study study : interview) {
            ReflectionTestUtils.setField(study, "createdAt", NOW.minusDays(day--));
            buffer.add(study);
        }

        for (Study study : programming) {
            ReflectionTestUtils.setField(study, "createdAt", NOW.minusDays(day--));
            buffer.add(study);
        }

        studyRepository.saveAll(buffer);
    }

    private void initDataWithFavorite() {
        initDataWithRegisterDate();

        favorites.clear();
        likeMarking(language[0], member[0], member[1], member[2], member[3], member[4], member[5], member[6]);
        likeMarking(language[1], member[0], member[1], member[2]);
        likeMarking(language[2], member[0], member[1], member[2]);
        likeMarking(language[3], member[0], member[1], member[2], member[3]);
        likeMarking(language[4]);
        likeMarking(language[5], member[0], member[1], member[2], member[3], member[4]);
        likeMarking(language[6], member[0], member[1], member[2]);

        likeMarking(interview[0], member[0]); // Offline
        likeMarking(interview[1], member[0], member[1], member[2], member[3]); // Offline
        likeMarking(interview[2], member[0], member[1]); // Offline
        likeMarking(interview[3], member[0], member[1], member[2], member[3], member[4], member[5], member[6]); // Offline
        likeMarking(interview[4], member[0], member[1], member[2], member[3]); // Offline

        likeMarking(programming[0], member[0], member[1], member[2], member[3], member[4]);
        likeMarking(programming[1], member[0], member[1], member[2]);
        likeMarking(programming[2], member[0], member[1], member[2], member[3], member[4], member[5], member[6], member[7]); // Offline
        likeMarking(programming[3], member[0], member[1], member[2], member[3], member[4], member[5], member[6], member[7]);
        likeMarking(programming[4], member[0]);
        likeMarking(programming[5], member[0], member[1], member[2], member[3], member[4], member[5], member[6]);
        likeMarking(programming[6], member[0], member[1], member[2], member[3], member[4], member[5], member[6]); // Offline
        likeMarking(programming[7], member[0], member[1], member[2], member[3], member[4]);
        likeMarking(programming[8], member[0], member[1], member[2], member[3], member[4], member[5]);
        likeMarking(programming[9], member[0], member[1], member[2], member[3], member[4], member[5], member[6], member[7], member[8]);
        likeMarking(programming[10], member[0], member[1], member[2], member[3]);
        likeMarking(programming[11], member[0], member[1], member[2], member[3]);
        favoriteJpaRepository.saveAll(favorites);
    }

    private void initDataWithReviews() {
        initDataWithRegisterDate();

        reviews.clear();
        writeReview(language[0], member[0], member[1], member[2], member[3], member[4], member[5], member[6]);
        writeReview(language[1], member[0], member[1], member[2]);
        writeReview(language[2], member[0], member[1], member[2]);
        writeReview(language[3], member[0], member[1], member[2], member[3]);
        writeReview(language[4]);
        writeReview(language[5], member[0], member[1], member[2], member[3], member[4]);
        writeReview(language[6], member[0], member[1], member[2]);
        writeReview(interview[0], member[0]); // Offline
        writeReview(interview[1], member[0], member[1], member[2], member[3]); // Offline
        writeReview(interview[2], member[0], member[1]); // Offline
        writeReview(interview[3], member[0], member[1], member[2], member[3], member[4], member[5], member[6]); // Offline
        writeReview(interview[4], member[0], member[1], member[2], member[3]); // Offline
        writeReview(programming[0], member[0], member[1], member[2], member[3], member[4]);
        writeReview(programming[1], member[0], member[1], member[2]);
        writeReview(programming[2], member[0], member[1], member[2], member[3], member[4], member[5], member[6], member[7]); // Offline
        writeReview(programming[3], member[0], member[1], member[2], member[3], member[4], member[5], member[6], member[7]);
        writeReview(programming[4], member[0]);
        writeReview(programming[5], member[0], member[1], member[2], member[3], member[4], member[5], member[6]);
        writeReview(programming[6], member[0], member[1], member[2], member[3], member[4], member[5], member[6]); // Offline
        writeReview(programming[7], member[0], member[1], member[2], member[3], member[4]);
        writeReview(programming[8], member[0], member[1], member[2], member[3], member[4], member[5]);
        writeReview(programming[9], member[0], member[1], member[2], member[3], member[4], member[5], member[6], member[7], member[8]);
        writeReview(programming[10], member[0], member[1], member[2], member[3]);
        writeReview(programming[11], member[0], member[1], member[2], member[3]);
        studyReviewRepository.saveAll(reviews);
    }

    private void likeMarking(final Study study, final Member... members) {
        for (int i = 0; i < members.length; i++) {
            study.addParticipant();
        }

        for (Member member : members) {
            favorites.add(Favorite.favoriteMarking(study.getId(), member.getId()));
        }
    }

    private void writeReview(final Study study, final Member... members) {
        for (int i = 0; i < members.length; i++) {
            study.addParticipant();
        }

        for (Member member : members) {
            reviews.add(StudyReview.writeReview(study.getId(), member.getId(), "Good Study"));
        }
    }

    private void assertThatStudiesMatch(
            final List<StudyPreview> previews,
            final List<Study> studies,
            final List<List<Member>> favoriteMarkingMembers
    ) {
        final int expectSize = studies.size();
        assertThat(previews).hasSize(expectSize);

        for (int i = 0; i < expectSize; i++) {
            final StudyPreview preview = previews.get(i);
            final Study expect = studies.get(i);
            final List<Long> likeMarkingMemberIds = favoriteMarkingMembers.get(i)
                    .stream()
                    .map(Member::getId)
                    .toList();

            assertAll(
                    () -> assertThat(preview.getId()).isEqualTo(expect.getId()),
                    () -> assertThat(preview.getName()).isEqualTo(expect.getNameValue()),
                    () -> assertThat(preview.getDescription()).isEqualTo(expect.getDescriptionValue()),
                    () -> assertThat(preview.getCategory()).isEqualTo(expect.getCategory().getName()),
                    () -> assertThat(preview.getThumbnail().name()).isEqualTo(expect.getThumbnail().getImageName()),
                    () -> assertThat(preview.getThumbnail().background()).isEqualTo(expect.getThumbnail().getBackground()),
                    () -> assertThat(preview.getType()).isEqualTo(expect.getType()),
                    () -> assertThat(preview.getRecruitmentStatus()).isEqualTo(expect.getRecruitmentStatus()),
                    () -> assertThat(preview.getMaxMember()).isEqualTo(expect.getCapacity()),
                    () -> assertThat(preview.getParticipantMembers()).isEqualTo(expect.getParticipantMembers()),
                    () -> assertThat(preview.getHashtags()).containsExactlyInAnyOrderElementsOf(expect.getHashtags()),
                    () -> assertThat(preview.getLikeMarkingMembers()).containsExactlyInAnyOrderElementsOf(likeMarkingMemberIds)
            );
        }
    }
}
