package com.kgu.studywithme.member.infrastructure.repository.query;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.favorite.domain.Favorite;
import com.kgu.studywithme.favorite.domain.FavoriteRepository;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.AppliedStudy;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.AttendanceRatio;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.GraduatedStudy;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.LikeMarkedStudy;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.MemberPrivateInformation;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.MemberPublicInformation;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.ParticipateStudy;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.ReceivedReview;
import com.kgu.studywithme.memberreview.domain.MemberReview;
import com.kgu.studywithme.memberreview.domain.MemberReviewRepository;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.studyattendance.domain.AttendanceStatus;
import com.kgu.studywithme.studyattendance.domain.StudyAttendance;
import com.kgu.studywithme.studyattendance.domain.StudyAttendanceRepository;
import com.kgu.studywithme.studyparticipant.domain.StudyParticipant;
import com.kgu.studywithme.studyparticipant.domain.StudyParticipantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY1;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY2;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY3;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY4;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY5;
import static com.kgu.studywithme.common.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.EFFECTIVE_JAVA;
import static com.kgu.studywithme.common.fixture.StudyFixture.JPA;
import static com.kgu.studywithme.common.fixture.StudyFixture.KOTLIN;
import static com.kgu.studywithme.common.fixture.StudyFixture.NETWORK;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.ABSENCE;
import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.ATTENDANCE;
import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.LATE;
import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.NON_ATTENDANCE;
import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.APPROVE;
import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.GRADUATED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member -> MemberInformationRepository 테스트")
class MemberInformationRepositoryTest extends RepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberReviewRepository memberReviewRepository;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private StudyParticipantRepository studyParticipantRepository;

    @Autowired
    private StudyAttendanceRepository studyAttendanceRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Nested
    @DisplayName("Only 사용자 관련 조회")
    class AboutMember {
        private Member member;

        @BeforeEach
        void setUp() {
            member = memberRepository.save(JIWON.toMember());
        }

        @Test
        @DisplayName("사용자 기본 Public 정보를 조회한다")
        void fetchPublicInformationById() {
            // when
            final MemberPublicInformation result = memberRepository.fetchPublicInformationById(member.getId());

            // then
            assertAll(
                    () -> assertThat(result.getId()).isEqualTo(member.getId()),
                    () -> assertThat(result.getName()).isEqualTo(member.getName()),
                    () -> assertThat(result.getNickname()).isEqualTo(member.getNicknameValue()),
                    () -> assertThat(result.getEmail()).isEqualTo(member.getEmailValue()),
                    () -> assertThat(result.getBirth()).isEqualTo(member.getBirth()),
                    () -> assertThat(result.getGender()).isEqualTo(member.getGender().getValue()),
                    () -> assertThat(result.getRegion()).isEqualTo(member.getRegion()),
                    () -> assertThat(result.getScore()).isEqualTo(member.getScore()),
                    () -> assertThat(result.getInterests())
                            .containsExactlyInAnyOrderElementsOf(
                                    member.getInterests()
                                            .stream()
                                            .map(Category::getName)
                                            .toList()
                            )
            );
        }

        @Test
        @DisplayName("사용자 기본 Private 정보를 조회한다")
        void fetchPrivateInformationById() {
            // when
            final MemberPrivateInformation result = memberRepository.fetchPrivateInformationById(member.getId());

            // then
            assertAll(
                    () -> assertThat(result.getId()).isEqualTo(member.getId()),
                    () -> assertThat(result.getName()).isEqualTo(member.getName()),
                    () -> assertThat(result.getNickname()).isEqualTo(member.getNicknameValue()),
                    () -> assertThat(result.getEmail()).isEqualTo(member.getEmailValue()),
                    () -> assertThat(result.getBirth()).isEqualTo(member.getBirth()),
                    () -> assertThat(result.getPhone()).isEqualTo(member.getPhone()),
                    () -> assertThat(result.getGender()).isEqualTo(member.getGender().getValue()),
                    () -> assertThat(result.getRegion()).isEqualTo(member.getRegion()),
                    () -> assertThat(result.getScore()).isEqualTo(member.getScore()),
                    () -> assertThat(result.isEmailOptIn()).isEqualTo(member.isEmailOptIn()),
                    () -> assertThat(result.getInterests())
                            .containsExactlyInAnyOrderElementsOf(
                                    member.getInterests()
                                            .stream()
                                            .map(Category::getName)
                                            .toList()
                            )
            );
        }

        @Test
        @DisplayName("받은 리뷰 내역을 조회한다")
        void fetchReceivedReviewById() {
            /* Review 3건 */
            final Member reviewerA = memberRepository.save(DUMMY1.toMember());
            final Member reviewerB = memberRepository.save(DUMMY2.toMember());
            final Member reviewerC = memberRepository.save(DUMMY3.toMember());
            memberReviewRepository.saveAll(
                    List.of(
                            MemberReview.doReview(
                                    reviewerA.getId(),
                                    member.getId(),
                                    "Good - " + reviewerA.getId()
                            ),
                            MemberReview.doReview(
                                    reviewerB.getId(),
                                    member.getId(),
                                    "Good - " + reviewerB.getId()
                            ),
                            MemberReview.doReview(
                                    reviewerC.getId(),
                                    member.getId(),
                                    "Good - " + reviewerC.getId()
                            )
                    )
            );

            final List<ReceivedReview> result1 = memberRepository.fetchReceivedReviewById(member.getId());
            assertAll(
                    () -> assertThat(result1).hasSize(3),
                    () -> assertThat(result1)
                            .map(ReceivedReview::content)
                            .containsExactly(
                                    "Good - " + reviewerC.getId(),
                                    "Good - " + reviewerB.getId(),
                                    "Good - " + reviewerA.getId()
                            )
            );

            /* 추가 Review 2건 */
            final Member reviewerD = memberRepository.save(DUMMY4.toMember());
            final Member reviewerE = memberRepository.save(DUMMY5.toMember());
            memberReviewRepository.saveAll(
                    List.of(
                            MemberReview.doReview(
                                    reviewerD.getId(),
                                    member.getId(),
                                    "Good - " + reviewerD.getId()
                            ),
                            MemberReview.doReview(
                                    reviewerE.getId(),
                                    member.getId(),
                                    "Good - " + reviewerE.getId()
                            )
                    )
            );

            final List<ReceivedReview> result2 = memberRepository.fetchReceivedReviewById(member.getId());
            assertAll(
                    () -> assertThat(result2).hasSize(5),
                    () -> assertThat(result2)
                            .map(ReceivedReview::content)
                            .containsExactly(
                                    "Good - " + reviewerE.getId(),
                                    "Good - " + reviewerD.getId(),
                                    "Good - " + reviewerC.getId(),
                                    "Good - " + reviewerB.getId(),
                                    "Good - " + reviewerA.getId()
                            )
            );
        }
    }

    @Nested
    @DisplayName("사용자 + 스터디 관련 조회")
    class AboutStudy {
        private Member member;
        private Study studyA;
        private Study studyB;
        private Study studyC;
        private Study studyD;
        private Study studyE;

        @BeforeEach
        void setUp() {
            member = memberRepository.save(JIWON.toMember());

            final Member host = memberRepository.save(GHOST.toMember());
            studyA = studyRepository.save(SPRING.toOnlineStudy(host.getId()));
            studyB = studyRepository.save(JPA.toOnlineStudy(host.getId()));
            studyC = studyRepository.save(KOTLIN.toOnlineStudy(host.getId()));
            studyD = studyRepository.save(NETWORK.toOnlineStudy(host.getId()));
            studyE = studyRepository.save(EFFECTIVE_JAVA.toOnlineStudy(host.getId()));
        }

        @Test
        @DisplayName("신청한 스터디 정보를 조회한다")
        void fetchAppliedStudyById() {
            /* studyA, studyB, studyC 신청 */
            studyParticipantRepository.saveAll(
                    List.of(
                            StudyParticipant.applyInStudy(studyA.getId(), member.getId()),
                            StudyParticipant.applyInStudy(studyB.getId(), member.getId()),
                            StudyParticipant.applyInStudy(studyC.getId(), member.getId())
                    )
            );

            final List<AppliedStudy> result1 = memberRepository.fetchAppliedStudyById(member.getId());
            assertThat(result1)
                    .map(AppliedStudy::id)
                    .containsExactly(studyC.getId(), studyB.getId(), studyA.getId());

            /* studyD, studyE 추가 신청 -> studyB 승인 */
            studyParticipantRepository.saveAll(
                    List.of(
                            StudyParticipant.applyInStudy(studyD.getId(), member.getId()),
                            StudyParticipant.applyInStudy(studyE.getId(), member.getId())
                    )
            );
            studyParticipantRepository.updateParticipantStatus(studyB.getId(), member.getId(), APPROVE);

            final List<AppliedStudy> result2 = memberRepository.fetchAppliedStudyById(member.getId());
            assertThat(result2)
                    .map(AppliedStudy::id)
                    .containsExactly(studyE.getId(), studyD.getId(), studyC.getId(), studyA.getId());
        }

        @Test
        @DisplayName("참여중인 스터디 정보를 조회한다")
        void fetchParticipateStudyById() {
            /* studyA, studyB, studyC 참여 */
            studyParticipantRepository.saveAll(
                    List.of(
                            StudyParticipant.applyParticipant(studyA.getId(), member.getId(), APPROVE),
                            StudyParticipant.applyParticipant(studyB.getId(), member.getId(), APPROVE),
                            StudyParticipant.applyParticipant(studyC.getId(), member.getId(), APPROVE)
                    )
            );

            final List<ParticipateStudy> result1 = memberRepository.fetchParticipateStudyById(member.getId());
            assertThat(result1)
                    .map(ParticipateStudy::id)
                    .containsExactly(studyC.getId(), studyB.getId(), studyA.getId());

            /* studyD, studyE 추가 참여 -> studyC 졸업 */
            studyParticipantRepository.saveAll(
                    List.of(
                            StudyParticipant.applyParticipant(studyD.getId(), member.getId(), APPROVE),
                            StudyParticipant.applyParticipant(studyE.getId(), member.getId(), APPROVE)
                    )
            );
            studyParticipantRepository.updateParticipantStatus(studyC.getId(), member.getId(), GRADUATED);

            final List<ParticipateStudy> result2 = memberRepository.fetchParticipateStudyById(member.getId());
            assertThat(result2)
                    .map(ParticipateStudy::id)
                    .containsExactly(studyE.getId(), studyD.getId(), studyB.getId(), studyA.getId());
        }

        @Test
        @DisplayName("졸업한 스터디 정보를 조회한다")
        void fetchGraduatedStudyById() {
            /* studyA, studyB, studyC 참여 */
            studyParticipantRepository.saveAll(
                    List.of(
                            StudyParticipant.applyParticipant(studyA.getId(), member.getId(), APPROVE),
                            StudyParticipant.applyParticipant(studyB.getId(), member.getId(), APPROVE),
                            StudyParticipant.applyParticipant(studyC.getId(), member.getId(), APPROVE)
                    )
            );

            final List<GraduatedStudy> result1 = memberRepository.fetchGraduatedStudyById(member.getId());
            assertThat(result1).isEmpty();

            /* studyD, studyE 추가 참여 -> studyA, studyC 졸업 */
            studyParticipantRepository.saveAll(
                    List.of(
                            StudyParticipant.applyParticipant(studyD.getId(), member.getId(), APPROVE),
                            StudyParticipant.applyParticipant(studyE.getId(), member.getId(), APPROVE)
                    )
            );
            studyParticipantRepository.updateParticipantStatus(studyA.getId(), member.getId(), GRADUATED);
            studyParticipantRepository.updateParticipantStatus(studyC.getId(), member.getId(), GRADUATED);

            final List<GraduatedStudy> result2 = memberRepository.fetchGraduatedStudyById(member.getId());
            assertThat(result2)
                    .map(GraduatedStudy::id)
                    .containsExactly(studyC.getId(), studyA.getId());
        }

        @Test
        @DisplayName("전체 스터디 출석률을 조회한다")
        void fetchAttendanceRatioById() {
            /* 모든 스터디 참여 */
            studyParticipantRepository.saveAll(
                    List.of(
                            StudyParticipant.applyParticipant(studyA.getId(), member.getId(), APPROVE),
                            StudyParticipant.applyParticipant(studyB.getId(), member.getId(), APPROVE),
                            StudyParticipant.applyParticipant(studyC.getId(), member.getId(), APPROVE),
                            StudyParticipant.applyParticipant(studyD.getId(), member.getId(), APPROVE),
                            StudyParticipant.applyParticipant(studyE.getId(), member.getId(), APPROVE)
                    )
            );

            /* Week 1 */
            studyAttendanceRepository.saveAll(
                    List.of(
                            StudyAttendance.recordAttendance(studyA.getId(), member.getId(), 1, ATTENDANCE),
                            StudyAttendance.recordAttendance(studyB.getId(), member.getId(), 1, ATTENDANCE),
                            StudyAttendance.recordAttendance(studyC.getId(), member.getId(), 1, LATE),
                            StudyAttendance.recordAttendance(studyD.getId(), member.getId(), 1, ATTENDANCE),
                            StudyAttendance.recordAttendance(studyE.getId(), member.getId(), 1, ABSENCE)
                    )
            );

            final List<AttendanceRatio> result1 = memberRepository.fetchAttendanceRatioById(member.getId());
            assertAll(
                    () -> assertThat(findCountByAttendanceStatus(result1, ATTENDANCE)).isEqualTo(3),
                    () -> assertThat(findCountByAttendanceStatus(result1, LATE)).isEqualTo(1),
                    () -> assertThat(findCountByAttendanceStatus(result1, ABSENCE)).isEqualTo(1),
                    () -> assertThat(findCountByAttendanceStatus(result1, NON_ATTENDANCE)).isEqualTo(0)
            );

            /* Week 2 */
            studyAttendanceRepository.saveAll(
                    List.of(
                            StudyAttendance.recordAttendance(studyA.getId(), member.getId(), 2, NON_ATTENDANCE),
                            StudyAttendance.recordAttendance(studyB.getId(), member.getId(), 2, ATTENDANCE),
                            StudyAttendance.recordAttendance(studyC.getId(), member.getId(), 2, NON_ATTENDANCE),
                            StudyAttendance.recordAttendance(studyD.getId(), member.getId(), 2, ATTENDANCE),
                            StudyAttendance.recordAttendance(studyE.getId(), member.getId(), 2, NON_ATTENDANCE)
                    )
            );

            final List<AttendanceRatio> result2 = memberRepository.fetchAttendanceRatioById(member.getId());
            assertAll(
                    () -> assertThat(findCountByAttendanceStatus(result2, ATTENDANCE)).isEqualTo(5),
                    () -> assertThat(findCountByAttendanceStatus(result2, LATE)).isEqualTo(1),
                    () -> assertThat(findCountByAttendanceStatus(result2, ABSENCE)).isEqualTo(1),
                    () -> assertThat(findCountByAttendanceStatus(result2, NON_ATTENDANCE)).isEqualTo(3)
            );
        }

        private int findCountByAttendanceStatus(
                final List<AttendanceRatio> attendanceRatios,
                final AttendanceStatus status
        ) {
            return attendanceRatios.stream()
                    .filter(ratio -> ratio.status() == status)
                    .findFirst()
                    .map(AttendanceRatio::count)
                    .orElse(0);
        }

        @Test
        @DisplayName("찜 등록한 스터디 정보를 조회한다")
        void fetchLikeMarkedStudyById() {
            /* studyA, studyB, studyC 찜 */
            favoriteRepository.saveAll(
                    List.of(
                            Favorite.favoriteMarking(studyA.getId(), member.getId()),
                            Favorite.favoriteMarking(studyB.getId(), member.getId()),
                            Favorite.favoriteMarking(studyC.getId(), member.getId())
                    )
            );

            final List<LikeMarkedStudy> result1 = memberRepository.fetchLikeMarkedStudyById(member.getId());
            assertThat(result1)
                    .map(LikeMarkedStudy::id)
                    .containsExactly(studyC.getId(), studyB.getId(), studyA.getId());

            /* studyD, studyE 추가 찜 */
            favoriteRepository.saveAll(
                    List.of(
                            Favorite.favoriteMarking(studyD.getId(), member.getId()),
                            Favorite.favoriteMarking(studyE.getId(), member.getId())
                    )
            );

            final List<LikeMarkedStudy> result2 = memberRepository.fetchLikeMarkedStudyById(member.getId());
            assertThat(result2)
                    .map(LikeMarkedStudy::id)
                    .containsExactly(studyE.getId(), studyD.getId(), studyC.getId(), studyB.getId(), studyA.getId());
        }
    }
}
