package com.kgu.studywithme.study.domain.repository.query;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.favorite.domain.model.Favorite;
import com.kgu.studywithme.favorite.domain.repository.FavoriteRepository;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.study.domain.repository.query.dto.StudyPreview;
import com.kgu.studywithme.study.utils.search.SearchByCategoryCondition;
import com.kgu.studywithme.study.utils.search.SearchByRecommendCondition;
import com.kgu.studywithme.studyreview.domain.model.StudyReview;
import com.kgu.studywithme.studyreview.domain.repository.StudyReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.kgu.studywithme.category.domain.model.Category.INTERVIEW;
import static com.kgu.studywithme.category.domain.model.Category.PROGRAMMING;
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
import static com.kgu.studywithme.global.query.PageCreator.query;
import static com.kgu.studywithme.study.domain.model.StudyType.OFFLINE;
import static com.kgu.studywithme.study.domain.model.StudyType.ONLINE;
import static com.kgu.studywithme.study.utils.search.SearchSortType.DATE;
import static com.kgu.studywithme.study.utils.search.SearchSortType.FAVORITE;
import static com.kgu.studywithme.study.utils.search.SearchSortType.REVIEW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(StudyCategorySearchRepositoryImpl.class)
@DisplayName("Study -> StudyCategorySearchRepository 테스트")
class StudyCategorySearchRepositoryTest extends RepositoryTest {
    @Autowired
    private StudyCategorySearchRepositoryImpl sut;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StudyReviewRepository studyReviewRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    private static final Pageable PAGE_REQUEST_1 = query(0);
    private static final Pageable PAGE_REQUEST_2 = query(1);
    private static final Pageable PAGE_REQUEST_3 = query(2);
    private static final LocalDateTime NOW = LocalDateTime.now();

    private Member host;
    private final Member[] members = new Member[9];
    private final Study[] language = new Study[7];
    private final Study[] interview = new Study[5];
    private final Study[] programming = new Study[12];
    private final List<Favorite> favorites = new ArrayList<>();
    private final List<StudyReview> reviews = new ArrayList<>();

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());

        members[0] = memberRepository.save(DUMMY1.toMember());
        members[1] = memberRepository.save(DUMMY2.toMember());
        members[2] = memberRepository.save(DUMMY3.toMember());
        members[3] = memberRepository.save(DUMMY4.toMember());
        members[4] = memberRepository.save(DUMMY5.toMember());
        members[5] = memberRepository.save(DUMMY6.toMember());
        members[6] = memberRepository.save(DUMMY7.toMember());
        members[7] = memberRepository.save(DUMMY8.toMember());
        members[8] = memberRepository.save(DUMMY9.toMember());

        language[0] = TOEIC.toStudy(host);
        language[1] = TOEFL.toStudy(host);
        language[2] = JAPANESE.toStudy(host);
        language[3] = CHINESE.toStudy(host);
        language[4] = FRENCH.toStudy(host);
        language[5] = GERMAN.toStudy(host);
        language[6] = ARABIC.toStudy(host);

        interview[0] = TOSS_INTERVIEW.toStudy(host);
        interview[1] = KAKAO_INTERVIEW.toStudy(host);
        interview[2] = NAVER_INTERVIEW.toStudy(host);
        interview[3] = LINE_INTERVIEW.toStudy(host);
        interview[4] = GOOGLE_INTERVIEW.toStudy(host);

        programming[0] = SPRING.toStudy(host);
        programming[1] = JPA.toStudy(host);
        programming[2] = REAL_MYSQL.toStudy(host);
        programming[3] = KOTLIN.toStudy(host);
        programming[4] = NETWORK.toStudy(host);
        programming[5] = EFFECTIVE_JAVA.toStudy(host);
        programming[6] = AWS.toStudy(host);
        programming[7] = DOCKER.toStudy(host);
        programming[8] = KUBERNETES.toStudy(host);
        programming[9] = PYTHON.toStudy(host);
        programming[10] = RUST.toStudy(host);
        programming[11] = OS.toStudy(host);
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
            final SearchByCategoryCondition onlineCondition = new SearchByCategoryCondition(
                    PROGRAMMING,
                    DATE,
                    ONLINE.getValue(),
                    null,
                    null
            );
            final Slice<StudyPreview> result1 = sut.fetchStudyByCategory(onlineCondition, PAGE_REQUEST_1);
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

            final Slice<StudyPreview> result2 = sut.fetchStudyByCategory(onlineCondition, PAGE_REQUEST_2);
            assertThatStudiesMatch(
                    result2.getContent(),
                    List.of(programming[1], programming[0]),
                    List.of(List.of(), List.of())
            );

            /* 오프라인 스터디 */
            final SearchByCategoryCondition offlineCondition = new SearchByCategoryCondition(
                    PROGRAMMING,
                    DATE,
                    OFFLINE.getValue(),
                    null,
                    null
            );
            final Slice<StudyPreview> result3 = sut.fetchStudyByCategory(offlineCondition, PAGE_REQUEST_1);
            assertThatStudiesMatch(
                    result3.getContent(),
                    List.of(programming[6], programming[2]),
                    List.of(List.of(), List.of())
            );

            /* 온라인 + 오프라인 통합 */
            final SearchByCategoryCondition totalCondition = new SearchByCategoryCondition(
                    PROGRAMMING,
                    DATE,
                    null,
                    null,
                    null
            );
            final Slice<StudyPreview> result4 = sut.fetchStudyByCategory(totalCondition, PAGE_REQUEST_1);
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

            final Slice<StudyPreview> result5 = sut.fetchStudyByCategory(totalCondition, PAGE_REQUEST_2);
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
            final SearchByCategoryCondition condition1 = new SearchByCategoryCondition(
                    INTERVIEW,
                    DATE,
                    OFFLINE.getValue(),
                    null,
                    "성남시"
            );
            final SearchByCategoryCondition condition2 = new SearchByCategoryCondition(
                    INTERVIEW,
                    DATE,
                    OFFLINE.getValue(),
                    "경기도",
                    "성남시"
            );
            final SearchByCategoryCondition condition3 = new SearchByCategoryCondition(
                    INTERVIEW,
                    DATE,
                    OFFLINE.getValue(),
                    "경기도",
                    null
            );

            // 서울 특별시 & 강남구
            final Slice<StudyPreview> result1 = sut.fetchStudyByCategory(condition1, PAGE_REQUEST_1);
            final Slice<StudyPreview> result2 = sut.fetchStudyByCategory(condition2, PAGE_REQUEST_1);
            final Slice<StudyPreview> result3 = sut.fetchStudyByCategory(condition3, PAGE_REQUEST_1);

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
            final SearchByCategoryCondition onlineCondition = new SearchByCategoryCondition(
                    PROGRAMMING,
                    FAVORITE,
                    ONLINE.getValue(),
                    null,
                    null
            );
            final Slice<StudyPreview> result1 = sut.fetchStudyByCategory(onlineCondition, PAGE_REQUEST_1);
            assertThatStudiesMatch(
                    result1.getContent(),
                    List.of(
                            programming[9], programming[3], programming[5], programming[8],
                            programming[7], programming[0], programming[11], programming[10]
                    ),
                    List.of(
                            List.of(
                                    members[0], members[1], members[2], members[3],
                                    members[4], members[5], members[6], members[7],
                                    members[8]
                            ),
                            List.of(
                                    members[0], members[1], members[2], members[3],
                                    members[4], members[5], members[6], members[7]
                            ),
                            List.of(
                                    members[0], members[1], members[2], members[3],
                                    members[4], members[5], members[6]
                            ),
                            List.of(
                                    members[0], members[1], members[2], members[3],
                                    members[4], members[5]
                            ),
                            List.of(
                                    members[0], members[1], members[2], members[3],
                                    members[4]
                            ),
                            List.of(
                                    members[0], members[1], members[2], members[3],
                                    members[4]
                            ),
                            List.of(members[0], members[1], members[2], members[3]),
                            List.of(members[0], members[1], members[2], members[3])
                    )
            );

            final Slice<StudyPreview> result2 = sut.fetchStudyByCategory(onlineCondition, PAGE_REQUEST_2);
            assertThatStudiesMatch(
                    result2.getContent(),
                    List.of(programming[1], programming[4]),
                    List.of(
                            List.of(members[0], members[1], members[2]),
                            List.of(members[0])
                    )
            );

            /* 오프라인 스터디 */
            final SearchByCategoryCondition offlineCondition = new SearchByCategoryCondition(
                    PROGRAMMING,
                    FAVORITE,
                    OFFLINE.getValue(),
                    null,
                    null
            );
            final Slice<StudyPreview> result3 = sut.fetchStudyByCategory(offlineCondition, PAGE_REQUEST_1);
            assertThatStudiesMatch(
                    result3.getContent(),
                    List.of(programming[2], programming[6]),
                    List.of(
                            List.of(
                                    members[0], members[1], members[2], members[3],
                                    members[4], members[5], members[6], members[7]
                            ),
                            List.of(
                                    members[0], members[1], members[2], members[3],
                                    members[4], members[5], members[6]
                            )
                    )
            );

            /* 온라인 + 오프라인 통합 */
            final SearchByCategoryCondition totalCondition = new SearchByCategoryCondition(
                    PROGRAMMING,
                    FAVORITE,
                    null,
                    null,
                    null
            );
            final Slice<StudyPreview> result4 = sut.fetchStudyByCategory(totalCondition, PAGE_REQUEST_1);
            assertThatStudiesMatch(
                    result4.getContent(),
                    List.of(
                            programming[9], programming[3], programming[2], programming[6],
                            programming[5], programming[8], programming[7], programming[0]
                    ),
                    List.of(
                            List.of(
                                    members[0], members[1], members[2], members[3],
                                    members[4], members[5], members[6], members[7],
                                    members[8]
                            ),
                            List.of(
                                    members[0], members[1], members[2], members[3],
                                    members[4], members[5], members[6], members[7]
                            ),
                            List.of(
                                    members[0], members[1], members[2], members[3],
                                    members[4], members[5], members[6], members[7]
                            ),
                            List.of(
                                    members[0], members[1], members[2], members[3],
                                    members[4], members[5], members[6]
                            ),
                            List.of(
                                    members[0], members[1], members[2], members[3],
                                    members[4], members[5], members[6]
                            ),
                            List.of(
                                    members[0], members[1], members[2], members[3],
                                    members[4], members[5]
                            ),
                            List.of(
                                    members[0], members[1], members[2], members[3],
                                    members[4]
                            ),
                            List.of(
                                    members[0], members[1], members[2], members[3],
                                    members[4]
                            )
                    )
            );

            final Slice<StudyPreview> result5 = sut.fetchStudyByCategory(totalCondition, PAGE_REQUEST_2);
            assertThatStudiesMatch(
                    result5.getContent(),
                    List.of(programming[11], programming[10], programming[1], programming[4]),
                    List.of(
                            List.of(members[0], members[1], members[2], members[3]),
                            List.of(members[0], members[1], members[2], members[3]),
                            List.of(members[0], members[1], members[2]),
                            List.of(members[0])
                    )
            );
        }

        @Test
        @DisplayName("리뷰가 많은 순으로 프로그래밍 스터디 리스트를 조회한다")
        void review() {
            // given
            initDataWithReviews();

            /* 온라인 스터디 */
            final SearchByCategoryCondition onlineCondition = new SearchByCategoryCondition(
                    PROGRAMMING,
                    REVIEW,
                    ONLINE.getValue(),
                    null,
                    null
            );
            final Slice<StudyPreview> result1 = sut.fetchStudyByCategory(onlineCondition, PAGE_REQUEST_1);
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

            final Slice<StudyPreview> result2 = sut.fetchStudyByCategory(onlineCondition, PAGE_REQUEST_2);
            assertThatStudiesMatch(
                    result2.getContent(),
                    List.of(programming[1], programming[4]),
                    List.of(List.of(), List.of())
            );

            /* 오프라인 스터디 */
            final SearchByCategoryCondition offlineCondition = new SearchByCategoryCondition(
                    PROGRAMMING,
                    REVIEW,
                    OFFLINE.getValue(),
                    null,
                    null
            );
            final Slice<StudyPreview> result3 = sut.fetchStudyByCategory(offlineCondition, PAGE_REQUEST_1);
            assertThatStudiesMatch(
                    result3.getContent(),
                    List.of(programming[2], programming[6]),
                    List.of(List.of(), List.of())
            );

            /* 온라인 + 오프라인 통합 */
            final SearchByCategoryCondition totalCondition = new SearchByCategoryCondition(
                    PROGRAMMING,
                    REVIEW,
                    null,
                    null,
                    null
            );
            final Slice<StudyPreview> result4 = sut.fetchStudyByCategory(totalCondition, PAGE_REQUEST_1);
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

            final Slice<StudyPreview> result5 = sut.fetchStudyByCategory(totalCondition, PAGE_REQUEST_2);
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
            final SearchByRecommendCondition onlineCondition = new SearchByRecommendCondition(
                    host.getId(),
                    DATE,
                    ONLINE.getValue(),
                    null,
                    null
            );
            final Slice<StudyPreview> result1 = sut.fetchStudyByRecommend(onlineCondition, PAGE_REQUEST_1);
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

            final Slice<StudyPreview> result2 = sut.fetchStudyByRecommend(onlineCondition, PAGE_REQUEST_2);
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

            final Slice<StudyPreview> result3 = sut.fetchStudyByRecommend(onlineCondition, PAGE_REQUEST_3);
            assertThatStudiesMatch(
                    result3.getContent(),
                    List.of(language[0]),
                    List.of(List.of())
            );

            /* 오프라인 스터디 */
            final SearchByRecommendCondition offlineCondition = new SearchByRecommendCondition(
                    host.getId(),
                    DATE,
                    OFFLINE.getValue(),
                    null,
                    null
            );
            final Slice<StudyPreview> result4 = sut.fetchStudyByRecommend(offlineCondition, PAGE_REQUEST_1);
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
            final SearchByRecommendCondition totalCondition = new SearchByRecommendCondition(
                    host.getId(),
                    DATE,
                    null,
                    null,
                    null
            );
            final Slice<StudyPreview> result5 = sut.fetchStudyByRecommend(totalCondition, PAGE_REQUEST_1);
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

            final Slice<StudyPreview> result6 = sut.fetchStudyByRecommend(totalCondition, PAGE_REQUEST_2);
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

            final Slice<StudyPreview> result7 = sut.fetchStudyByRecommend(totalCondition, PAGE_REQUEST_3);
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
            final SearchByRecommendCondition condition1 = new SearchByRecommendCondition(
                    host.getId(),
                    DATE,
                    OFFLINE.getValue(),
                    "서울특별시",
                    "강남구"
            );
            final SearchByRecommendCondition condition2 = new SearchByRecommendCondition(
                    host.getId(),
                    DATE,
                    OFFLINE.getValue(),
                    null,
                    "강남구"
            );
            final SearchByRecommendCondition condition3 = new SearchByRecommendCondition(
                    host.getId(),
                    DATE,
                    OFFLINE.getValue(),
                    "서울특별시",
                    null
            );

            // 서울 특별시 & 강남구
            final Slice<StudyPreview> result1 = sut.fetchStudyByRecommend(condition1, PAGE_REQUEST_1);
            final Slice<StudyPreview> result2 = sut.fetchStudyByRecommend(condition2, PAGE_REQUEST_1);
            final Slice<StudyPreview> result3 = sut.fetchStudyByRecommend(condition3, PAGE_REQUEST_1);

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
            final SearchByRecommendCondition onlineCondition = new SearchByRecommendCondition(
                    host.getId(),
                    FAVORITE,
                    ONLINE.getValue(),
                    null,
                    null
            );
            final Slice<StudyPreview> result1 = sut.fetchStudyByRecommend(onlineCondition, PAGE_REQUEST_1);
            assertThatStudiesMatch(
                    result1.getContent(),
                    List.of(
                            programming[9], programming[3], programming[5], language[0],
                            programming[8], programming[7], programming[0], language[5]
                    ),
                    List.of(
                            List.of(
                                    members[0], members[1], members[2], members[3],
                                    members[4], members[5], members[6], members[7], members[8]
                            ),
                            List.of(
                                    members[0], members[1], members[2], members[3],
                                    members[4], members[5], members[6], members[7]
                            ),
                            List.of(
                                    members[0], members[1], members[2], members[3],
                                    members[4], members[5], members[6]
                            ),
                            List.of(
                                    members[0], members[1], members[2], members[3],
                                    members[4], members[5], members[6]
                            ),
                            List.of(
                                    members[0], members[1], members[2], members[3],
                                    members[4], members[5]
                            ),
                            List.of(
                                    members[0], members[1], members[2], members[3],
                                    members[4]
                            ),
                            List.of(
                                    members[0], members[1], members[2], members[3],
                                    members[4]
                            ),
                            List.of(
                                    members[0], members[1], members[2], members[3],
                                    members[4]
                            )
                    )
            );

            final Slice<StudyPreview> result2 = sut.fetchStudyByRecommend(onlineCondition, PAGE_REQUEST_2);
            assertThatStudiesMatch(
                    result2.getContent(),
                    List.of(
                            programming[11], programming[10], language[3], programming[1],
                            language[6], language[2], language[1], programming[4]
                    ),
                    List.of(
                            List.of(members[0], members[1], members[2], members[3]),
                            List.of(members[0], members[1], members[2], members[3]),
                            List.of(members[0], members[1], members[2], members[3]),
                            List.of(members[0], members[1], members[2]),
                            List.of(members[0], members[1], members[2]),
                            List.of(members[0], members[1], members[2]),
                            List.of(members[0], members[1], members[2]),
                            List.of(members[0])
                    )
            );

            final Slice<StudyPreview> result3 = sut.fetchStudyByRecommend(onlineCondition, PAGE_REQUEST_3);
            assertThatStudiesMatch(
                    result3.getContent(),
                    List.of(language[4]),
                    List.of(List.of())
            );

            /* 오프라인 스터디 */
            final SearchByRecommendCondition offlineCondition = new SearchByRecommendCondition(
                    host.getId(),
                    FAVORITE,
                    OFFLINE.getValue(),
                    null,
                    null
            );
            final Slice<StudyPreview> result4 = sut.fetchStudyByRecommend(offlineCondition, PAGE_REQUEST_1);
            assertThatStudiesMatch(
                    result4.getContent(),
                    List.of(
                            programming[2], programming[6], interview[3], interview[4],
                            interview[1], interview[2], interview[0]
                    ),
                    List.of(
                            List.of(
                                    members[0], members[1], members[2], members[3],
                                    members[4], members[5], members[6], members[7]
                            ),
                            List.of(
                                    members[0], members[1], members[2], members[3],
                                    members[4], members[5], members[6]
                            ),
                            List.of(
                                    members[0], members[1], members[2], members[3],
                                    members[4], members[5], members[6]
                            ),
                            List.of(members[0], members[1], members[2], members[3]),
                            List.of(members[0], members[1], members[2], members[3]),
                            List.of(members[0], members[1]),
                            List.of(members[0])
                    )
            );

            /* 온라인 + 오프라인 통합 */
            final SearchByRecommendCondition totalCondition = new SearchByRecommendCondition(
                    host.getId(),
                    FAVORITE,
                    null,
                    null,
                    null
            );
            final Slice<StudyPreview> result5 = sut.fetchStudyByRecommend(totalCondition, PAGE_REQUEST_1);
            assertThatStudiesMatch(
                    result5.getContent(),
                    List.of(
                            programming[9], programming[3], programming[2], programming[6],
                            programming[5], interview[3], language[0], programming[8]
                    ),
                    List.of(
                            List.of(
                                    members[0], members[1], members[2], members[3],
                                    members[4], members[5], members[6], members[7], members[8]
                            ),
                            List.of(
                                    members[0], members[1], members[2], members[3],
                                    members[4], members[5], members[6], members[7]
                            ),
                            List.of(
                                    members[0], members[1], members[2], members[3],
                                    members[4], members[5], members[6], members[7]
                            ),
                            List.of(
                                    members[0], members[1], members[2], members[3],
                                    members[4], members[5], members[6]
                            ),
                            List.of(
                                    members[0], members[1], members[2], members[3],
                                    members[4], members[5], members[6]
                            ),
                            List.of(
                                    members[0], members[1], members[2], members[3],
                                    members[4], members[5], members[6]
                            ),
                            List.of(
                                    members[0], members[1], members[2], members[3],
                                    members[4], members[5], members[6]
                            ),
                            List.of(
                                    members[0], members[1], members[2], members[3],
                                    members[4], members[5]
                            )
                    )
            );

            final Slice<StudyPreview> result6 = sut.fetchStudyByRecommend(totalCondition, PAGE_REQUEST_2);
            assertThatStudiesMatch(
                    result6.getContent(),
                    List.of(
                            programming[7], programming[0], language[5], programming[11],
                            programming[10], interview[4], interview[1], language[3]
                    ),
                    List.of(
                            List.of(
                                    members[0], members[1], members[2], members[3],
                                    members[4]
                            ),
                            List.of(
                                    members[0], members[1], members[2], members[3],
                                    members[4]
                            ),
                            List.of(
                                    members[0], members[1], members[2], members[3],
                                    members[4]
                            ),
                            List.of(members[0], members[1], members[2], members[3]),
                            List.of(members[0], members[1], members[2], members[3]),
                            List.of(members[0], members[1], members[2], members[3]),
                            List.of(members[0], members[1], members[2], members[3]),
                            List.of(members[0], members[1], members[2], members[3])
                    )
            );

            final Slice<StudyPreview> result7 = sut.fetchStudyByRecommend(totalCondition, PAGE_REQUEST_3);
            assertThatStudiesMatch(
                    result7.getContent(),
                    List.of(
                            programming[1], language[6], language[2], language[1],
                            interview[2], programming[4], interview[0], language[4]
                    ),
                    List.of(
                            List.of(members[0], members[1], members[2]),
                            List.of(members[0], members[1], members[2]),
                            List.of(members[0], members[1], members[2]),
                            List.of(members[0], members[1], members[2]),
                            List.of(members[0], members[1]),
                            List.of(members[0]),
                            List.of(members[0]),
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
            final SearchByRecommendCondition onlineCondition = new SearchByRecommendCondition(
                    host.getId(),
                    REVIEW,
                    ONLINE.getValue(),
                    null,
                    null
            );
            final Slice<StudyPreview> result1 = sut.fetchStudyByRecommend(onlineCondition, PAGE_REQUEST_1);
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

            final Slice<StudyPreview> result2 = sut.fetchStudyByRecommend(onlineCondition, PAGE_REQUEST_2);
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

            final Slice<StudyPreview> result3 = sut.fetchStudyByRecommend(onlineCondition, PAGE_REQUEST_3);
            assertThatStudiesMatch(
                    result3.getContent(),
                    List.of(language[4]),
                    List.of(List.of())
            );

            /* 오프라인 스터디 */
            final SearchByRecommendCondition offlineCondition = new SearchByRecommendCondition(
                    host.getId(),
                    REVIEW,
                    OFFLINE.getValue(),
                    null,
                    null
            );
            final Slice<StudyPreview> result4 = sut.fetchStudyByRecommend(offlineCondition, PAGE_REQUEST_1);
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
            final SearchByRecommendCondition totalCondition = new SearchByRecommendCondition(
                    host.getId(),
                    REVIEW,
                    null,
                    null,
                    null
            );
            final Slice<StudyPreview> result5 = sut.fetchStudyByRecommend(totalCondition, PAGE_REQUEST_1);
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

            final Slice<StudyPreview> result6 = sut.fetchStudyByRecommend(totalCondition, PAGE_REQUEST_2);
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

            final Slice<StudyPreview> result7 = sut.fetchStudyByRecommend(totalCondition, PAGE_REQUEST_3);
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
        likeMarking(language[0], members[0], members[1], members[2], members[3], members[4], members[5], members[6]);
        likeMarking(language[1], members[0], members[1], members[2]);
        likeMarking(language[2], members[0], members[1], members[2]);
        likeMarking(language[3], members[0], members[1], members[2], members[3]);
        likeMarking(language[4]);
        likeMarking(language[5], members[0], members[1], members[2], members[3], members[4]);
        likeMarking(language[6], members[0], members[1], members[2]);

        likeMarking(interview[0], members[0]); // Offline
        likeMarking(interview[1], members[0], members[1], members[2], members[3]); // Offline
        likeMarking(interview[2], members[0], members[1]); // Offline
        likeMarking(interview[3], members[0], members[1], members[2], members[3], members[4], members[5], members[6]); // Offline
        likeMarking(interview[4], members[0], members[1], members[2], members[3]); // Offline

        likeMarking(programming[0], members[0], members[1], members[2], members[3], members[4]);
        likeMarking(programming[1], members[0], members[1], members[2]);
        likeMarking(programming[2], members[0], members[1], members[2], members[3], members[4], members[5], members[6], members[7]); // Offline
        likeMarking(programming[3], members[0], members[1], members[2], members[3], members[4], members[5], members[6], members[7]);
        likeMarking(programming[4], members[0]);
        likeMarking(programming[5], members[0], members[1], members[2], members[3], members[4], members[5], members[6]);
        likeMarking(programming[6], members[0], members[1], members[2], members[3], members[4], members[5], members[6]); // Offline
        likeMarking(programming[7], members[0], members[1], members[2], members[3], members[4]);
        likeMarking(programming[8], members[0], members[1], members[2], members[3], members[4], members[5]);
        likeMarking(programming[9], members[0], members[1], members[2], members[3], members[4], members[5], members[6], members[7], members[8]);
        likeMarking(programming[10], members[0], members[1], members[2], members[3]);
        likeMarking(programming[11], members[0], members[1], members[2], members[3]);
        favoriteRepository.saveAll(favorites);
    }

    private void initDataWithReviews() {
        initDataWithRegisterDate();

        reviews.clear();
        writeReview(language[0], members[0], members[1], members[2], members[3], members[4], members[5], members[6]);
        writeReview(language[1], members[0], members[1], members[2]);
        writeReview(language[2], members[0], members[1], members[2]);
        writeReview(language[3], members[0], members[1], members[2], members[3]);
        writeReview(language[4]);
        writeReview(language[5], members[0], members[1], members[2], members[3], members[4]);
        writeReview(language[6], members[0], members[1], members[2]);
        writeReview(interview[0], members[0]); // Offline
        writeReview(interview[1], members[0], members[1], members[2], members[3]); // Offline
        writeReview(interview[2], members[0], members[1]); // Offline
        writeReview(interview[3], members[0], members[1], members[2], members[3], members[4], members[5], members[6]); // Offline
        writeReview(interview[4], members[0], members[1], members[2], members[3]); // Offline
        writeReview(programming[0], members[0], members[1], members[2], members[3], members[4]);
        writeReview(programming[1], members[0], members[1], members[2]);
        writeReview(programming[2], members[0], members[1], members[2], members[3], members[4], members[5], members[6], members[7]); // Offline
        writeReview(programming[3], members[0], members[1], members[2], members[3], members[4], members[5], members[6], members[7]);
        writeReview(programming[4], members[0]);
        writeReview(programming[5], members[0], members[1], members[2], members[3], members[4], members[5], members[6]);
        writeReview(programming[6], members[0], members[1], members[2], members[3], members[4], members[5], members[6]); // Offline
        writeReview(programming[7], members[0], members[1], members[2], members[3], members[4]);
        writeReview(programming[8], members[0], members[1], members[2], members[3], members[4], members[5]);
        writeReview(programming[9], members[0], members[1], members[2], members[3], members[4], members[5], members[6], members[7], members[8]);
        writeReview(programming[10], members[0], members[1], members[2], members[3]);
        writeReview(programming[11], members[0], members[1], members[2], members[3]);
        studyReviewRepository.saveAll(reviews);
    }

    private void likeMarking(final Study study, final Member... members) {
        for (int i = 0; i < members.length; i++) {
            study.addParticipant();
        }

        for (Member member : members) {
            favorites.add(Favorite.favoriteMarking(member, study));
            study.increaseFavoriteCount();
        }
    }

    private void writeReview(final Study study, final Member... members) {
        for (int i = 0; i < members.length; i++) {
            study.addParticipant();
        }

        for (Member member : members) {
            reviews.add(StudyReview.writeReview(study, member, "Good Study"));
            study.increaseReviewCount();
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
                    () -> assertThat(preview.getName()).isEqualTo(expect.getName().getValue()),
                    () -> assertThat(preview.getDescription()).isEqualTo(expect.getDescription().getValue()),
                    () -> assertThat(preview.getCategory()).isEqualTo(expect.getCategory().getName()),
                    () -> assertThat(preview.getThumbnail().name()).isEqualTo(expect.getThumbnail().getImageName()),
                    () -> assertThat(preview.getThumbnail().background()).isEqualTo(expect.getThumbnail().getBackground()),
                    () -> assertThat(preview.getType()).isEqualTo(expect.getType()),
                    () -> assertThat(preview.getRecruitmentStatus()).isEqualTo(expect.getRecruitmentStatus()),
                    () -> assertThat(preview.getMaxMember()).isEqualTo(expect.getCapacity().getValue()),
                    () -> assertThat(preview.getParticipantMembers()).isEqualTo(expect.getParticipants()),
                    () -> assertThat(preview.getHashtags()).containsExactlyInAnyOrderElementsOf(expect.getHashtags()),
                    () -> assertThat(preview.getLikeMarkingMembers()).containsExactlyInAnyOrderElementsOf(likeMarkingMemberIds)
            );
        }
    }
}
