package com.kgu.studywithme.member.domain.repository.query;

import com.kgu.studywithme.category.domain.model.Category;
import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.favorite.domain.model.Favorite;
import com.kgu.studywithme.favorite.domain.repository.FavoriteRepository;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.member.domain.repository.query.dto.AppliedStudy;
import com.kgu.studywithme.member.domain.repository.query.dto.AttendanceRatio;
import com.kgu.studywithme.member.domain.repository.query.dto.GraduatedStudy;
import com.kgu.studywithme.member.domain.repository.query.dto.LikeMarkedStudy;
import com.kgu.studywithme.member.domain.repository.query.dto.MemberPrivateInformation;
import com.kgu.studywithme.member.domain.repository.query.dto.MemberPublicInformation;
import com.kgu.studywithme.member.domain.repository.query.dto.ParticipateStudy;
import com.kgu.studywithme.member.domain.repository.query.dto.ReceivedReview;
import com.kgu.studywithme.memberreview.domain.model.MemberReview;
import com.kgu.studywithme.memberreview.domain.repository.MemberReviewRepository;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus;
import com.kgu.studywithme.studyattendance.domain.model.StudyAttendance;
import com.kgu.studywithme.studyattendance.domain.repository.StudyAttendanceRepository;
import com.kgu.studywithme.studyparticipant.domain.model.StudyParticipant;
import com.kgu.studywithme.studyparticipant.domain.repository.StudyParticipantRepository;
import com.kgu.studywithme.studyreview.domain.model.StudyReview;
import com.kgu.studywithme.studyreview.domain.repository.StudyReviewRepository;
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
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.ABSENCE;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.ATTENDANCE;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.LATE;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.NON_ATTENDANCE;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.APPROVE;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.GRADUATED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(MemberInformationRepositoryImpl.class)
@DisplayName("Member -> MemberInformationRepository 테스트")
class MemberInformationRepositoryTest extends RepositoryTest {
    @Autowired
    private MemberInformationRepositoryImpl sut;

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

    @Autowired
    private StudyReviewRepository studyReviewRepository;

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
            final MemberPublicInformation result = sut.fetchPublicInformationById(member.getId());

            // then
            assertAll(
                    () -> assertThat(result.getId()).isEqualTo(member.getId()),
                    () -> assertThat(result.getName()).isEqualTo(member.getName()),
                    () -> assertThat(result.getNickname()).isEqualTo(member.getNickname().getValue()),
                    () -> assertThat(result.getEmail()).isEqualTo(member.getEmail().getValue()),
                    () -> assertThat(result.getBirth()).isEqualTo(member.getBirth()),
                    () -> assertThat(result.getGender()).isEqualTo(member.getGender().getValue()),
                    () -> assertThat(result.getAddress().province()).isEqualTo(member.getAddress().getProvince()),
                    () -> assertThat(result.getAddress().city()).isEqualTo(member.getAddress().getCity()),
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
            final MemberPrivateInformation result = sut.fetchPrivateInformationById(member.getId());

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
                    () -> assertThat(result.getAddress().province()).isEqualTo(member.getAddress().getProvince()),
                    () -> assertThat(result.getAddress().city()).isEqualTo(member.getAddress().getCity()),
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
            final Member reviewerA = memberRepository.save(DUMMY1.toMember());
            final Member reviewerB = memberRepository.save(DUMMY2.toMember());
            final Member reviewerC = memberRepository.save(DUMMY3.toMember());
            memberReviewRepository.saveAll(List.of(
                    MemberReview.doReview(reviewerA, member, "Good - " + reviewerA.getId()),
                    MemberReview.doReview(reviewerB, member, "Good - " + reviewerB.getId()),
                    MemberReview.doReview(reviewerC, member, "Good - " + reviewerC.getId())
            ));

            final List<ReceivedReview> result1 = sut.fetchReceivedReviewById(member.getId());
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
            memberReviewRepository.saveAll(List.of(
                    MemberReview.doReview(reviewerD, member, "Good - " + reviewerD.getId()),
                    MemberReview.doReview(reviewerE, member, "Good - " + reviewerE.getId())
            ));

            final List<ReceivedReview> result2 = sut.fetchReceivedReviewById(member.getId());
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
            studyA = studyRepository.save(SPRING.toStudy(host));
            studyB = studyRepository.save(JPA.toStudy(host));
            studyC = studyRepository.save(KOTLIN.toStudy(host));
            studyD = studyRepository.save(NETWORK.toStudy(host));
            studyE = studyRepository.save(EFFECTIVE_JAVA.toStudy(host));
        }

        @Test
        @DisplayName("신청한 스터디 정보를 조회한다")
        void fetchAppliedStudyById() {
            /* studyA, studyB, studyC 신청 */
            studyParticipantRepository.saveAll(List.of(
                    StudyParticipant.applyInStudy(studyA, member),
                    StudyParticipant.applyInStudy(studyB, member),
                    StudyParticipant.applyInStudy(studyC, member)
            ));

            final List<AppliedStudy> result1 = sut.fetchAppliedStudyById(member.getId());
            assertThat(result1)
                    .map(AppliedStudy::id)
                    .containsExactly(studyC.getId(), studyB.getId(), studyA.getId());

            /* studyD, studyE 추가 신청 -> studyB 승인 */
            studyParticipantRepository.saveAll(List.of(
                    StudyParticipant.applyInStudy(studyD, member),
                    StudyParticipant.applyInStudy(studyE, member)
            ));
            studyParticipantRepository.updateParticipantStatus(studyB.getId(), member.getId(), APPROVE);

            final List<AppliedStudy> result2 = sut.fetchAppliedStudyById(member.getId());
            assertThat(result2)
                    .map(AppliedStudy::id)
                    .containsExactly(studyE.getId(), studyD.getId(), studyC.getId(), studyA.getId());
        }

        @Test
        @DisplayName("참여중인 스터디 정보를 조회한다")
        void fetchParticipateStudyById() {
            /* studyA, studyB, studyC 참여 */
            studyParticipantRepository.saveAll(List.of(
                    StudyParticipant.applyParticipant(studyA, member, APPROVE),
                    StudyParticipant.applyParticipant(studyB, member, APPROVE),
                    StudyParticipant.applyParticipant(studyC, member, APPROVE)
            ));

            final List<ParticipateStudy> result1 = sut.fetchParticipateStudyById(member.getId());
            assertThat(result1)
                    .map(ParticipateStudy::id)
                    .containsExactly(studyC.getId(), studyB.getId(), studyA.getId());

            /* studyD, studyE 추가 참여 -> studyC 졸업 */
            studyParticipantRepository.saveAll(List.of(
                    StudyParticipant.applyParticipant(studyD, member, APPROVE),
                    StudyParticipant.applyParticipant(studyE, member, APPROVE)
            ));
            studyParticipantRepository.updateParticipantStatus(studyC.getId(), member.getId(), GRADUATED);

            final List<ParticipateStudy> result2 = sut.fetchParticipateStudyById(member.getId());
            assertThat(result2)
                    .map(ParticipateStudy::id)
                    .containsExactly(studyE.getId(), studyD.getId(), studyB.getId(), studyA.getId());
        }

        @Test
        @DisplayName("졸업한 스터디 정보를 조회한다")
        void fetchGraduatedStudyById() {
            /* studyA, studyB, studyC 참여 */
            studyParticipantRepository.saveAll(List.of(
                    StudyParticipant.applyParticipant(studyA, member, APPROVE),
                    StudyParticipant.applyParticipant(studyB, member, APPROVE),
                    StudyParticipant.applyParticipant(studyC, member, APPROVE)
            ));

            final List<GraduatedStudy> result1 = sut.fetchGraduatedStudyById(member.getId());
            assertThat(result1).isEmpty();

            /* studyD, studyE 추가 참여 -> studyA, studyC 졸업 */
            studyParticipantRepository.saveAll(List.of(
                    StudyParticipant.applyParticipant(studyD, member, APPROVE),
                    StudyParticipant.applyParticipant(studyE, member, APPROVE)
            ));
            studyParticipantRepository.updateParticipantStatus(studyA.getId(), member.getId(), GRADUATED);
            studyParticipantRepository.updateParticipantStatus(studyC.getId(), member.getId(), GRADUATED);

            final List<GraduatedStudy> result2 = sut.fetchGraduatedStudyById(member.getId());
            assertAll(
                    () -> assertThat(result2.get(0).getId()).isEqualTo(studyC.getId()),
                    () -> assertThat(result2.get(0).getReview()).isNull(),
                    () -> assertThat(result2.get(1).getId()).isEqualTo(studyA.getId()),
                    () -> assertThat(result2.get(1).getReview()).isNull()
            );

            /* member -> studyA 리뷰 작성 */
            studyReviewRepository.save(StudyReview.writeReview(studyA, member, "Good!"));

            final List<GraduatedStudy> result3 = sut.fetchGraduatedStudyById(member.getId());
            assertAll(
                    () -> assertThat(result3.get(0).getId()).isEqualTo(studyC.getId()),
                    () -> assertThat(result3.get(0).getReview()).isNull(),
                    () -> assertThat(result3.get(1).getId()).isEqualTo(studyA.getId()),
                    () -> assertThat(result3.get(1).getReview()).isNotNull()
            );
        }

        @Test
        @DisplayName("전체 스터디 출석률을 조회한다")
        void fetchAttendanceRatioById() {
            /* 모든 스터디 참여 */
            studyParticipantRepository.saveAll(List.of(
                    StudyParticipant.applyParticipant(studyA, member, APPROVE),
                    StudyParticipant.applyParticipant(studyB, member, APPROVE),
                    StudyParticipant.applyParticipant(studyC, member, APPROVE),
                    StudyParticipant.applyParticipant(studyD, member, APPROVE),
                    StudyParticipant.applyParticipant(studyE, member, APPROVE)
            ));

            /* Week 1 */
            studyAttendanceRepository.saveAll(List.of(
                    StudyAttendance.recordAttendance(studyA, member, 1, ATTENDANCE),
                    StudyAttendance.recordAttendance(studyB, member, 1, ATTENDANCE),
                    StudyAttendance.recordAttendance(studyC, member, 1, LATE),
                    StudyAttendance.recordAttendance(studyD, member, 1, ATTENDANCE),
                    StudyAttendance.recordAttendance(studyE, member, 1, ABSENCE)
            ));

            final List<AttendanceRatio> result1 = sut.fetchAttendanceRatioById(member.getId());
            assertAll(
                    () -> assertThat(findCountByAttendanceStatus(result1, ATTENDANCE)).isEqualTo(3),
                    () -> assertThat(findCountByAttendanceStatus(result1, LATE)).isEqualTo(1),
                    () -> assertThat(findCountByAttendanceStatus(result1, ABSENCE)).isEqualTo(1),
                    () -> assertThat(findCountByAttendanceStatus(result1, NON_ATTENDANCE)).isEqualTo(0)
            );

            /* Week 2 */
            studyAttendanceRepository.saveAll(List.of(
                    StudyAttendance.recordAttendance(studyA, member, 2, NON_ATTENDANCE),
                    StudyAttendance.recordAttendance(studyB, member, 2, ATTENDANCE),
                    StudyAttendance.recordAttendance(studyC, member, 2, NON_ATTENDANCE),
                    StudyAttendance.recordAttendance(studyD, member, 2, ATTENDANCE),
                    StudyAttendance.recordAttendance(studyE, member, 2, NON_ATTENDANCE)
            ));

            final List<AttendanceRatio> result2 = sut.fetchAttendanceRatioById(member.getId());
            assertAll(
                    () -> assertThat(findCountByAttendanceStatus(result2, ATTENDANCE)).isEqualTo(5),
                    () -> assertThat(findCountByAttendanceStatus(result2, LATE)).isEqualTo(1),
                    () -> assertThat(findCountByAttendanceStatus(result2, ABSENCE)).isEqualTo(1),
                    () -> assertThat(findCountByAttendanceStatus(result2, NON_ATTENDANCE)).isEqualTo(3)
            );
        }

        private long findCountByAttendanceStatus(
                final List<AttendanceRatio> attendanceRatios,
                final AttendanceStatus status
        ) {
            return attendanceRatios.stream()
                    .filter(ratio -> ratio.status() == status)
                    .findFirst()
                    .map(AttendanceRatio::count)
                    .orElse(0L);
        }

        @Test
        @DisplayName("찜 등록한 스터디 정보를 조회한다")
        void fetchLikeMarkedStudyById() {
            /* studyA, studyB, studyC 찜 */
            favoriteRepository.saveAll(List.of(
                    Favorite.favoriteMarking(member, studyA),
                    Favorite.favoriteMarking(member, studyB),
                    Favorite.favoriteMarking(member, studyC)
            ));

            final List<LikeMarkedStudy> result1 = sut.fetchLikeMarkedStudyById(member.getId());
            assertThat(result1)
                    .map(LikeMarkedStudy::id)
                    .containsExactly(studyC.getId(), studyB.getId(), studyA.getId());

            /* studyD, studyE 추가 찜 */
            favoriteRepository.saveAll(List.of(
                    Favorite.favoriteMarking(member, studyD),
                    Favorite.favoriteMarking(member, studyE)
            ));

            final List<LikeMarkedStudy> result2 = sut.fetchLikeMarkedStudyById(member.getId());
            assertThat(result2)
                    .map(LikeMarkedStudy::id)
                    .containsExactly(studyE.getId(), studyD.getId(), studyC.getId(), studyB.getId(), studyA.getId());
        }
    }
}
