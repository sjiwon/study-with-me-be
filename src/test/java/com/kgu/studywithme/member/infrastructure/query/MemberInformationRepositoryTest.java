package com.kgu.studywithme.member.infrastructure.query;

import com.kgu.studywithme.category.domain.Category;
import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.favorite.domain.Favorite;
import com.kgu.studywithme.favorite.infrastructure.persistence.FavoriteJpaRepository;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.infrastructure.persistence.MemberJpaRepository;
import com.kgu.studywithme.member.infrastructure.query.dto.AppliedStudy;
import com.kgu.studywithme.member.infrastructure.query.dto.AttendanceRatio;
import com.kgu.studywithme.member.infrastructure.query.dto.GraduatedStudy;
import com.kgu.studywithme.member.infrastructure.query.dto.LikeMarkedStudy;
import com.kgu.studywithme.member.infrastructure.query.dto.MemberPrivateInformation;
import com.kgu.studywithme.member.infrastructure.query.dto.MemberPublicInformation;
import com.kgu.studywithme.member.infrastructure.query.dto.ParticipateStudy;
import com.kgu.studywithme.member.infrastructure.query.dto.ReceivedReview;
import com.kgu.studywithme.memberreview.domain.MemberReview;
import com.kgu.studywithme.memberreview.infrastructure.persistence.MemberReviewJpaRepository;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.infrastructure.persistence.StudyJpaRepository;
import com.kgu.studywithme.studyattendance.domain.AttendanceStatus;
import com.kgu.studywithme.studyattendance.domain.StudyAttendance;
import com.kgu.studywithme.studyattendance.infrastructure.persistence.StudyAttendanceJpaRepository;
import com.kgu.studywithme.studyparticipant.domain.StudyParticipant;
import com.kgu.studywithme.studyparticipant.infrastructure.persistence.StudyParticipantJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

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

@Import(MemberInformationRepository.class)
@DisplayName("Member -> MemberInformationRepository 테스트")
class MemberInformationRepositoryTest extends RepositoryTest {
    @Autowired
    private MemberInformationRepository memberInformationRepository;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private MemberReviewJpaRepository memberReviewJpaRepository;

    @Autowired
    private StudyJpaRepository studyJpaRepository;

    @Autowired
    private StudyParticipantJpaRepository studyParticipantJpaRepository;

    @Autowired
    private StudyAttendanceJpaRepository studyAttendanceJpaRepository;

    @Autowired
    private FavoriteJpaRepository favoriteJpaRepository;

    @Nested
    @DisplayName("Only 사용자 관련 조회")
    class AboutMember {
        private Member member;

        @BeforeEach
        void setUp() {
            member = memberJpaRepository.save(JIWON.toMember());
        }

        @Test
        @DisplayName("사용자 기본 Public 정보를 조회한다")
        void fetchPublicInformationById() {
            // when
            final MemberPublicInformation result = memberInformationRepository.fetchPublicInformationById(member.getId());

            // then
            assertAll(
                    () -> assertThat(result.getId()).isEqualTo(member.getId()),
                    () -> assertThat(result.getName()).isEqualTo(member.getName()),
                    () -> assertThat(result.getNickname()).isEqualTo(member.getNickname().getValue()),
                    () -> assertThat(result.getEmail()).isEqualTo(member.getEmail().getValue()),
                    () -> assertThat(result.getBirth()).isEqualTo(member.getBirth()),
                    () -> assertThat(result.getGender()).isEqualTo(member.getGender().getValue()),
                    () -> assertThat(result.getAddress()).isEqualTo(member.getAddress()),
                    () -> assertThat(result.getScore()).isEqualTo(member.getScore().getValue()),
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
            final MemberPrivateInformation result = memberInformationRepository.fetchPrivateInformationById(member.getId());

            // then
            assertAll(
                    () -> assertThat(result.getId()).isEqualTo(member.getId()),
                    () -> assertThat(result.getName()).isEqualTo(member.getName()),
                    () -> assertThat(result.getNickname()).isEqualTo(member.getNickname().getValue()),
                    () -> assertThat(result.getEmail()).isEqualTo(member.getEmail().getValue()),
                    () -> assertThat(result.isEmailOptIn()).isEqualTo(member.isEmailOptIn()),
                    () -> assertThat(result.getBirth()).isEqualTo(member.getBirth()),
                    () -> assertThat(result.getPhone()).isEqualTo(member.getPhone().getValue()),
                    () -> assertThat(result.getGender()).isEqualTo(member.getGender().getValue()),
                    () -> assertThat(result.getAddress()).isEqualTo(member.getAddress()),
                    () -> assertThat(result.getScore()).isEqualTo(member.getScore().getValue()),
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
            final Member reviewerA = memberJpaRepository.save(DUMMY1.toMember());
            final Member reviewerB = memberJpaRepository.save(DUMMY2.toMember());
            final Member reviewerC = memberJpaRepository.save(DUMMY3.toMember());
            memberReviewJpaRepository.saveAll(
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

            final List<ReceivedReview> result1 = memberInformationRepository.fetchReceivedReviewById(member.getId());
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
            final Member reviewerD = memberJpaRepository.save(DUMMY4.toMember());
            final Member reviewerE = memberJpaRepository.save(DUMMY5.toMember());
            memberReviewJpaRepository.saveAll(
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

            final List<ReceivedReview> result2 = memberInformationRepository.fetchReceivedReviewById(member.getId());
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
            member = memberJpaRepository.save(JIWON.toMember());

            final Member host = memberJpaRepository.save(GHOST.toMember());
            studyA = studyJpaRepository.save(SPRING.toOnlineStudy(host.getId()));
            studyB = studyJpaRepository.save(JPA.toOnlineStudy(host.getId()));
            studyC = studyJpaRepository.save(KOTLIN.toOnlineStudy(host.getId()));
            studyD = studyJpaRepository.save(NETWORK.toOnlineStudy(host.getId()));
            studyE = studyJpaRepository.save(EFFECTIVE_JAVA.toOnlineStudy(host.getId()));
        }

        @Test
        @DisplayName("신청한 스터디 정보를 조회한다")
        void fetchAppliedStudyById() {
            /* studyA, studyB, studyC 신청 */
            studyParticipantJpaRepository.saveAll(
                    List.of(
                            StudyParticipant.applyInStudy(studyA.getId(), member.getId()),
                            StudyParticipant.applyInStudy(studyB.getId(), member.getId()),
                            StudyParticipant.applyInStudy(studyC.getId(), member.getId())
                    )
            );

            final List<AppliedStudy> result1 = memberInformationRepository.fetchAppliedStudyById(member.getId());
            assertThat(result1)
                    .map(AppliedStudy::id)
                    .containsExactly(studyC.getId(), studyB.getId(), studyA.getId());

            /* studyD, studyE 추가 신청 -> studyB 승인 */
            studyParticipantJpaRepository.saveAll(
                    List.of(
                            StudyParticipant.applyInStudy(studyD.getId(), member.getId()),
                            StudyParticipant.applyInStudy(studyE.getId(), member.getId())
                    )
            );
            studyParticipantJpaRepository.updateParticipantStatus(studyB.getId(), member.getId(), APPROVE);

            final List<AppliedStudy> result2 = memberInformationRepository.fetchAppliedStudyById(member.getId());
            assertThat(result2)
                    .map(AppliedStudy::id)
                    .containsExactly(studyE.getId(), studyD.getId(), studyC.getId(), studyA.getId());
        }

        @Test
        @DisplayName("참여중인 스터디 정보를 조회한다")
        void fetchParticipateStudyById() {
            /* studyA, studyB, studyC 참여 */
            studyParticipantJpaRepository.saveAll(
                    List.of(
                            StudyParticipant.applyParticipant(studyA.getId(), member.getId(), APPROVE),
                            StudyParticipant.applyParticipant(studyB.getId(), member.getId(), APPROVE),
                            StudyParticipant.applyParticipant(studyC.getId(), member.getId(), APPROVE)
                    )
            );

            final List<ParticipateStudy> result1 = memberInformationRepository.fetchParticipateStudyById(member.getId());
            assertThat(result1)
                    .map(ParticipateStudy::id)
                    .containsExactly(studyC.getId(), studyB.getId(), studyA.getId());

            /* studyD, studyE 추가 참여 -> studyC 졸업 */
            studyParticipantJpaRepository.saveAll(
                    List.of(
                            StudyParticipant.applyParticipant(studyD.getId(), member.getId(), APPROVE),
                            StudyParticipant.applyParticipant(studyE.getId(), member.getId(), APPROVE)
                    )
            );
            studyParticipantJpaRepository.updateParticipantStatus(studyC.getId(), member.getId(), GRADUATED);

            final List<ParticipateStudy> result2 = memberInformationRepository.fetchParticipateStudyById(member.getId());
            assertThat(result2)
                    .map(ParticipateStudy::id)
                    .containsExactly(studyE.getId(), studyD.getId(), studyB.getId(), studyA.getId());
        }

        @Test
        @DisplayName("졸업한 스터디 정보를 조회한다")
        void fetchGraduatedStudyById() {
            /* studyA, studyB, studyC 참여 */
            studyParticipantJpaRepository.saveAll(
                    List.of(
                            StudyParticipant.applyParticipant(studyA.getId(), member.getId(), APPROVE),
                            StudyParticipant.applyParticipant(studyB.getId(), member.getId(), APPROVE),
                            StudyParticipant.applyParticipant(studyC.getId(), member.getId(), APPROVE)
                    )
            );

            final List<GraduatedStudy> result1 = memberInformationRepository.fetchGraduatedStudyById(member.getId());
            assertThat(result1).isEmpty();

            /* studyD, studyE 추가 참여 -> studyA, studyC 졸업 */
            studyParticipantJpaRepository.saveAll(
                    List.of(
                            StudyParticipant.applyParticipant(studyD.getId(), member.getId(), APPROVE),
                            StudyParticipant.applyParticipant(studyE.getId(), member.getId(), APPROVE)
                    )
            );
            studyParticipantJpaRepository.updateParticipantStatus(studyA.getId(), member.getId(), GRADUATED);
            studyParticipantJpaRepository.updateParticipantStatus(studyC.getId(), member.getId(), GRADUATED);

            final List<GraduatedStudy> result2 = memberInformationRepository.fetchGraduatedStudyById(member.getId());
            assertThat(result2)
                    .map(GraduatedStudy::id)
                    .containsExactly(studyC.getId(), studyA.getId());
        }

        @Test
        @DisplayName("전체 스터디 출석률을 조회한다")
        void fetchAttendanceRatioById() {
            /* 모든 스터디 참여 */
            studyParticipantJpaRepository.saveAll(
                    List.of(
                            StudyParticipant.applyParticipant(studyA.getId(), member.getId(), APPROVE),
                            StudyParticipant.applyParticipant(studyB.getId(), member.getId(), APPROVE),
                            StudyParticipant.applyParticipant(studyC.getId(), member.getId(), APPROVE),
                            StudyParticipant.applyParticipant(studyD.getId(), member.getId(), APPROVE),
                            StudyParticipant.applyParticipant(studyE.getId(), member.getId(), APPROVE)
                    )
            );

            /* Week 1 */
            studyAttendanceJpaRepository.saveAll(
                    List.of(
                            StudyAttendance.recordAttendance(studyA.getId(), member.getId(), 1, ATTENDANCE),
                            StudyAttendance.recordAttendance(studyB.getId(), member.getId(), 1, ATTENDANCE),
                            StudyAttendance.recordAttendance(studyC.getId(), member.getId(), 1, LATE),
                            StudyAttendance.recordAttendance(studyD.getId(), member.getId(), 1, ATTENDANCE),
                            StudyAttendance.recordAttendance(studyE.getId(), member.getId(), 1, ABSENCE)
                    )
            );

            final List<AttendanceRatio> result1 = memberInformationRepository.fetchAttendanceRatioById(member.getId());
            assertAll(
                    () -> assertThat(findCountByAttendanceStatus(result1, ATTENDANCE)).isEqualTo(3),
                    () -> assertThat(findCountByAttendanceStatus(result1, LATE)).isEqualTo(1),
                    () -> assertThat(findCountByAttendanceStatus(result1, ABSENCE)).isEqualTo(1),
                    () -> assertThat(findCountByAttendanceStatus(result1, NON_ATTENDANCE)).isEqualTo(0)
            );

            /* Week 2 */
            studyAttendanceJpaRepository.saveAll(
                    List.of(
                            StudyAttendance.recordAttendance(studyA.getId(), member.getId(), 2, NON_ATTENDANCE),
                            StudyAttendance.recordAttendance(studyB.getId(), member.getId(), 2, ATTENDANCE),
                            StudyAttendance.recordAttendance(studyC.getId(), member.getId(), 2, NON_ATTENDANCE),
                            StudyAttendance.recordAttendance(studyD.getId(), member.getId(), 2, ATTENDANCE),
                            StudyAttendance.recordAttendance(studyE.getId(), member.getId(), 2, NON_ATTENDANCE)
                    )
            );

            final List<AttendanceRatio> result2 = memberInformationRepository.fetchAttendanceRatioById(member.getId());
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
            favoriteJpaRepository.saveAll(
                    List.of(
                            Favorite.favoriteMarking(member.getId(), studyA.getId()),
                            Favorite.favoriteMarking(member.getId(), studyB.getId()),
                            Favorite.favoriteMarking(member.getId(), studyC.getId())
                    )
            );

            final List<LikeMarkedStudy> result1 = memberInformationRepository.fetchLikeMarkedStudyById(member.getId());
            assertThat(result1)
                    .map(LikeMarkedStudy::id)
                    .containsExactly(studyC.getId(), studyB.getId(), studyA.getId());

            /* studyD, studyE 추가 찜 */
            favoriteJpaRepository.saveAll(
                    List.of(
                            Favorite.favoriteMarking(member.getId(), studyD.getId()),
                            Favorite.favoriteMarking(member.getId(), studyE.getId())
                    )
            );

            final List<LikeMarkedStudy> result2 = memberInformationRepository.fetchLikeMarkedStudyById(member.getId());
            assertThat(result2)
                    .map(LikeMarkedStudy::id)
                    .containsExactly(studyE.getId(), studyD.getId(), studyC.getId(), studyB.getId(), studyA.getId());
        }
    }
}
