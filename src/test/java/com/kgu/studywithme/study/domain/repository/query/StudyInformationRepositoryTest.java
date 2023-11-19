package com.kgu.studywithme.study.domain.repository.query;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.study.domain.repository.query.dto.AttendanceInformation;
import com.kgu.studywithme.study.domain.repository.query.dto.NoticeInformation;
import com.kgu.studywithme.study.domain.repository.query.dto.ReviewInformation;
import com.kgu.studywithme.study.domain.repository.query.dto.StudyApplicantInformation;
import com.kgu.studywithme.study.domain.repository.query.dto.StudyBasicInformation;
import com.kgu.studywithme.study.domain.repository.query.dto.StudyMember;
import com.kgu.studywithme.study.domain.repository.query.dto.StudyParticipantInformation;
import com.kgu.studywithme.study.domain.repository.query.dto.WeeklyInformation;
import com.kgu.studywithme.studyattendance.domain.model.StudyAttendance;
import com.kgu.studywithme.studyattendance.domain.repository.StudyAttendanceRepository;
import com.kgu.studywithme.studynotice.domain.model.StudyNotice;
import com.kgu.studywithme.studynotice.domain.repository.StudyNoticeRepository;
import com.kgu.studywithme.studyparticipant.domain.model.StudyParticipant;
import com.kgu.studywithme.studyparticipant.domain.repository.StudyParticipantRepository;
import com.kgu.studywithme.studyreview.domain.model.StudyReview;
import com.kgu.studywithme.studyreview.domain.repository.StudyReviewRepository;
import com.kgu.studywithme.studyweekly.domain.model.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.model.UploadAssignment;
import com.kgu.studywithme.studyweekly.domain.model.UploadAttachment;
import com.kgu.studywithme.studyweekly.domain.repository.StudyWeeklyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY1;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY2;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY3;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_1;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_2;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_5;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.ATTENDANCE;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.LATE;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.NON_ATTENDANCE;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.APPROVE;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.GRADUATED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(StudyInformationRepositoryImpl.class)
@DisplayName("Study -> StudyInformationRepository 테스트")
class StudyInformationRepositoryTest extends RepositoryTest {
    @Autowired
    private StudyInformationRepositoryImpl sut;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StudyReviewRepository studyReviewRepository;

    @Autowired
    private StudyParticipantRepository studyParticipantRepository;

    @Autowired
    private StudyNoticeRepository studyNoticeRepository;

    @Autowired
    private StudyAttendanceRepository studyAttendanceRepository;

    @Autowired
    private StudyWeeklyRepository studyWeeklyRepository;

    private Member host;
    private Member memberA;
    private Member memberB;
    private Member memberC;
    private Study study;

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
        memberA = memberRepository.save(DUMMY1.toMember());
        memberB = memberRepository.save(DUMMY2.toMember());
        memberC = memberRepository.save(DUMMY3.toMember());

        study = studyRepository.save(SPRING.toStudy(host));
    }

    @Test
    @DisplayName("스터디 기본 정보를 조회한다")
    void fetchBasicInformationById() {
        /* host, memberA, memberB 참여 승인 & memberC 신청 대기 */
        studyParticipantRepository.saveAll(List.of(
                StudyParticipant.applyHost(study, host),
                StudyParticipant.applyParticipant(study, memberA, APPROVE),
                StudyParticipant.applyParticipant(study, memberB, APPROVE),
                StudyParticipant.applyInStudy(study, memberC)
        ));
        IntStream.range(0, 2).forEach(i -> study.addParticipant());

        final StudyBasicInformation result = sut.fetchBasicInformationById(study.getId());
        assertAll(
                () -> assertThat(result.getId()).isEqualTo(study.getId()),
                () -> assertThat(result.getName()).isEqualTo(study.getName().getValue()),
                () -> assertThat(result.getDescription()).isEqualTo(study.getDescription().getValue()),
                () -> assertThat(result.getCategory()).isEqualTo(study.getCategory().getName()),
                () -> assertThat(result.getThumbnail().name()).isEqualTo(study.getThumbnail().getImageName()),
                () -> assertThat(result.getThumbnail().background()).isEqualTo(study.getThumbnail().getBackground()),
                () -> assertThat(result.getType()).isEqualTo(study.getType()),
                () -> assertThat(result.getLocation()).isEqualTo(study.getLocation()),
                () -> assertThat(result.getRecruitmentStatus()).isEqualTo(study.getRecruitmentStatus()),
                () -> assertThat(result.getMaxMember()).isEqualTo(study.getCapacity().getValue()),
                () -> assertThat(result.getParticipantMembers()).isEqualTo(3), // host, memberA, memberB
                () -> assertThat(result.getMinimumAttendanceForGraduation()).isEqualTo(study.getGraduationPolicy().getMinimumAttendance()),
                () -> assertThat(result.getRemainingOpportunityToUpdateGraduationPolicy()).isEqualTo(study.getGraduationPolicy().getUpdateChance()),
                () -> assertThat(result.getHost().id()).isEqualTo(host.getId()),
                () -> assertThat(result.getHost().nickname()).isEqualTo(host.getNickname().getValue()),
                () -> assertThat(result.getHashtags()).containsExactlyInAnyOrderElementsOf(study.getHashtags()),
                () -> assertThat(result.getParticipants())
                        .map(StudyBasicInformation.ParticipantInformation::id)
                        .containsExactlyInAnyOrder(host.getId(), memberA.getId(), memberB.getId()) // host, memberA, memberB
        );
    }

    @Test
    @DisplayName("스터디 리뷰를 조회한다")
    void fetchReviewById() {
        /* 졸업자 3명 */
        studyParticipantRepository.saveAll(List.of(
                StudyParticipant.applyHost(study, host),
                StudyParticipant.applyParticipant(study, memberA, GRADUATED),
                StudyParticipant.applyParticipant(study, memberB, GRADUATED),
                StudyParticipant.applyParticipant(study, memberC, GRADUATED)
        ));

        /* 리뷰 2건 */
        studyReviewRepository.saveAll(List.of(
                StudyReview.writeReview(study, memberA, "Good Study"),
                StudyReview.writeReview(study, memberB, "Good Study")
        ));

        final ReviewInformation result1 = sut.fetchReviewById(study.getId());
        assertAll(
                () -> assertThat(result1.reviews()).hasSize(2),
                () -> assertThat(result1.reviews())
                        .map(ReviewInformation.ReviewMetadata::reviewer)
                        .map(StudyMember::id)
                        .containsExactly(memberB.getId(), memberA.getId()),
                () -> assertThat(result1.graduateCount()).isEqualTo(3)
        );

        /* 리뷰 추가 1건 */
        studyReviewRepository.save(StudyReview.writeReview(study, memberC, "Good Study"));

        final ReviewInformation result2 = sut.fetchReviewById(study.getId());
        assertAll(
                () -> assertThat(result2.reviews()).hasSize(3),
                () -> assertThat(result2.reviews())
                        .map(ReviewInformation.ReviewMetadata::reviewer)
                        .map(StudyMember::id)
                        .containsExactly(memberC.getId(), memberB.getId(), memberA.getId()),
                () -> assertThat(result2.graduateCount()).isEqualTo(3)
        );
    }

    @Test
    @DisplayName("스터디 참여자를 조회한다")
    void fetchParticipantById() {
        /* 신청자 3명 */
        studyParticipantRepository.saveAll(List.of(
                StudyParticipant.applyHost(study, host),
                StudyParticipant.applyInStudy(study, memberA),
                StudyParticipant.applyInStudy(study, memberB),
                StudyParticipant.applyInStudy(study, memberC)
        ));

        final StudyParticipantInformation result1 = sut.fetchParticipantById(study.getId());
        assertAll(
                () -> assertThat(result1.host().id()).isEqualTo(host.getId()),
                () -> assertThat(result1.participants()).isEmpty()
        );

        /* memberA, memberC 참여 승인 */
        studyParticipantRepository.updateParticipantStatus(study.getId(), memberA.getId(), APPROVE);
        studyParticipantRepository.updateParticipantStatus(study.getId(), memberC.getId(), APPROVE);
        IntStream.range(0, 2).forEach(i -> study.addParticipant());

        final StudyParticipantInformation result2 = sut.fetchParticipantById(study.getId());
        assertAll(
                () -> assertThat(result2.host().id()).isEqualTo(host.getId()),
                () -> assertThat(result2.participants())
                        .map(StudyMember::id)
                        .containsExactlyInAnyOrder(memberA.getId(), memberC.getId())
        );

        /* memberB 참여 승인 */
        studyParticipantRepository.updateParticipantStatus(study.getId(), memberB.getId(), APPROVE);
        IntStream.range(0, 1).forEach(i -> study.addParticipant());

        final StudyParticipantInformation result3 = sut.fetchParticipantById(study.getId());
        assertAll(
                () -> assertThat(result3.host().id()).isEqualTo(host.getId()),
                () -> assertThat(result3.participants())
                        .map(StudyMember::id)
                        .containsExactlyInAnyOrder(memberA.getId(), memberB.getId(), memberC.getId())
        );

        /* memberC 졸업 */
        studyParticipantRepository.updateParticipantStatus(study.getId(), memberC.getId(), GRADUATED);
        IntStream.range(0, 1).forEach(i -> study.removeParticipant());

        final StudyParticipantInformation result4 = sut.fetchParticipantById(study.getId());
        assertAll(
                () -> assertThat(result4.host().id()).isEqualTo(host.getId()),
                () -> assertThat(result4.participants())
                        .map(StudyMember::id)
                        .containsExactlyInAnyOrder(memberA.getId(), memberB.getId())
        );
    }

    @Test
    @DisplayName("스터디 신청자를 조회한다")
    void fetchApplicantById() {
        /* 신청자 3명 */
        studyParticipantRepository.saveAll(List.of(
                StudyParticipant.applyHost(study, host),
                StudyParticipant.applyInStudy(study, memberA),
                StudyParticipant.applyInStudy(study, memberB),
                StudyParticipant.applyInStudy(study, memberC)
        ));

        final List<StudyApplicantInformation> result1 = sut.fetchApplicantById(study.getId());
        assertAll(
                () -> assertThat(result1).hasSize(3),
                () -> assertThat(result1)
                        .map(StudyApplicantInformation::id)
                        .containsExactly(memberC.getId(), memberB.getId(), memberA.getId())
        );

        /* memberA, memberC 참여 승인 */
        studyParticipantRepository.updateParticipantStatus(study.getId(), memberA.getId(), APPROVE);
        studyParticipantRepository.updateParticipantStatus(study.getId(), memberC.getId(), APPROVE);
        IntStream.range(0, 2).forEach(i -> study.addParticipant());

        final List<StudyApplicantInformation> result2 = sut.fetchApplicantById(study.getId());
        assertAll(
                () -> assertThat(result2).hasSize(1),
                () -> assertThat(result2)
                        .map(StudyApplicantInformation::id)
                        .containsExactly(memberB.getId())
        );

        /* memberB 참여 승인 */
        studyParticipantRepository.updateParticipantStatus(study.getId(), memberB.getId(), APPROVE);
        IntStream.range(0, 1).forEach(i -> study.addParticipant());

        final List<StudyApplicantInformation> result3 = sut.fetchApplicantById(study.getId());
        assertThat(result3).isEmpty();
    }

    @Test
    @DisplayName("스터디 공지사항을 조회한다")
    void fetchNoticeById() {
        /* 공지사항 2건 */
        final StudyNotice notice1 = studyNoticeRepository.save(StudyNotice.writeNotice(study, host, "Notice 1", "Notice 1 Content"));
        notice1.addComment(memberA, "OK");
        notice1.addComment(memberB, "OK");
        notice1.addComment(memberC, "OK");

        final StudyNotice notice2 = studyNoticeRepository.save(StudyNotice.writeNotice(study, host, "Notice 2", "Notice 2 Content"));
        notice2.addComment(memberA, "OK");
        notice2.addComment(memberC, "OK");

        final List<NoticeInformation> result = sut.fetchNoticeById(study.getId());
        assertAll(
                () -> assertThat(result).hasSize(2),
                () -> assertThat(result)
                        .map(NoticeInformation::getId)
                        .containsExactly(notice2.getId(), notice1.getId()),
                () -> assertThat(result)
                        .map(NoticeInformation::getWriter)
                        .map(StudyMember::id)
                        .containsExactly(host.getId(), host.getId()),

                // Weekly2 Comments
                () -> assertThat(result.get(0).getComments()).hasSize(2),
                () -> assertThat(result.get(0).getComments())
                        .map(NoticeInformation.CommentInformation::writer)
                        .map(StudyMember::id)
                        .containsExactly(memberA.getId(), memberC.getId()),

                // Weely1 Comments
                () -> assertThat(result.get(1).getComments()).hasSize(3),
                () -> assertThat(result.get(1).getComments())
                        .map(NoticeInformation.CommentInformation::writer)
                        .map(StudyMember::id)
                        .containsExactly(memberA.getId(), memberB.getId(), memberC.getId())
        );
    }

    @Test
    @DisplayName("스터디 참여자들의 출석 정보를 조회한다 [Only Approved Participant]")
    void fetchAttendanceById() {
        /* host, memberA, memberC 참여 승인 & memberB 신청 대기 */
        studyParticipantRepository.saveAll(List.of(
                StudyParticipant.applyHost(study, host).apply(1L, LocalDateTime.now().minusDays(4)),
                StudyParticipant.applyParticipant(study, memberA, APPROVE).apply(2L, LocalDateTime.now().minusDays(3)),
                StudyParticipant.applyInStudy(study, memberB).apply(3L, LocalDateTime.now().minusDays(2)),
                StudyParticipant.applyParticipant(study, memberC, APPROVE).apply(4L, LocalDateTime.now().minusDays(1))
        ));
        IntStream.range(0, 2).forEach(i -> study.addParticipant());

        studyAttendanceRepository.saveAll(List.of(
                StudyAttendance.recordAttendance(study, host, 1, ATTENDANCE),
                StudyAttendance.recordAttendance(study, host, 2, ATTENDANCE),
                StudyAttendance.recordAttendance(study, host, 3, ATTENDANCE),
                StudyAttendance.recordAttendance(study, host, 4, ATTENDANCE),

                StudyAttendance.recordAttendance(study, memberA, 1, ATTENDANCE),
                StudyAttendance.recordAttendance(study, memberA, 2, LATE),
                StudyAttendance.recordAttendance(study, memberA, 3, ATTENDANCE),
                StudyAttendance.recordAttendance(study, memberA, 4, NON_ATTENDANCE),

                StudyAttendance.recordAttendance(study, memberC, 1, ATTENDANCE),
                StudyAttendance.recordAttendance(study, memberC, 2, ATTENDANCE),
                StudyAttendance.recordAttendance(study, memberC, 3, LATE)
        ));

        final List<AttendanceInformation> result = sut.fetchAttendanceById(study.getId());
        assertAll(
                () -> assertThat(result).hasSize(3),
                () -> assertThat(result)
                        .map(AttendanceInformation::member)
                        .map(StudyMember::id)
                        .containsExactly(host.getId(), memberA.getId(), memberC.getId())
        );

        final List<AttendanceInformation.AttendanceSummary> summaryOfHost = extractSummaryByMemberId(result, host.getId());
        assertAll(
                () -> assertThat(summaryOfHost).hasSize(4),
                () -> assertThat(summaryOfHost)
                        .map(AttendanceInformation.AttendanceSummary::week)
                        .containsExactly(1, 2, 3, 4),
                () -> assertThat(summaryOfHost)
                        .map(AttendanceInformation.AttendanceSummary::attendanceStatus)
                        .containsExactly(
                                ATTENDANCE.getValue(),
                                ATTENDANCE.getValue(),
                                ATTENDANCE.getValue(),
                                ATTENDANCE.getValue()
                        )
        );

        final List<AttendanceInformation.AttendanceSummary> summaryOfMemberA = extractSummaryByMemberId(result, memberA.getId());
        assertAll(
                () -> assertThat(summaryOfMemberA).hasSize(4),
                () -> assertThat(summaryOfMemberA)
                        .map(AttendanceInformation.AttendanceSummary::week)
                        .containsExactly(1, 2, 3, 4),
                () -> assertThat(summaryOfMemberA)
                        .map(AttendanceInformation.AttendanceSummary::attendanceStatus)
                        .containsExactly(
                                ATTENDANCE.getValue(),
                                LATE.getValue(),
                                ATTENDANCE.getValue(),
                                NON_ATTENDANCE.getValue()
                        )
        );

        final List<AttendanceInformation.AttendanceSummary> summaryOfMemberC = extractSummaryByMemberId(result, memberC.getId());
        assertAll(
                () -> assertThat(summaryOfMemberC).hasSize(3),
                () -> assertThat(summaryOfMemberC)
                        .map(AttendanceInformation.AttendanceSummary::week)
                        .containsExactly(1, 2, 3),
                () -> assertThat(summaryOfMemberC)
                        .map(AttendanceInformation.AttendanceSummary::attendanceStatus)
                        .containsExactly(
                                ATTENDANCE.getValue(),
                                ATTENDANCE.getValue(),
                                LATE.getValue()
                        )
        );
    }

    private List<AttendanceInformation.AttendanceSummary> extractSummaryByMemberId(
            final List<AttendanceInformation> result,
            final Long memberId
    ) {
        return result.stream()
                .filter(attendanceInformation -> attendanceInformation.member().id().equals(memberId))
                .findFirst()
                .map(AttendanceInformation::summaries)
                .orElse(List.of());
    }

    @Test
    @DisplayName("스터디 주차별 정보를 조회한다")
    void fetchWeeklyById() {
        /* WeeklyA, WeeklyB = 과제 O / WeeklyC = 과제 X */
        final StudyWeekly weeklyA = studyWeeklyRepository.save(STUDY_WEEKLY_1.toWeeklyWithAssignment(study.getId(), host.getId()));
        weeklyA.submitAssignment(memberA.getId(), UploadAssignment.withLink("https://notion.so/weeklyA/memberA"));
        weeklyA.submitAssignment(memberB.getId(), UploadAssignment.withLink("https://notion.so/weeklyA/memberB"));
        weeklyA.submitAssignment(memberC.getId(), UploadAssignment.withLink("https://notion.so/weeklyA/memberC"));

        final StudyWeekly weeklyB = studyWeeklyRepository.save(STUDY_WEEKLY_2.toWeeklyWithAssignment(study.getId(), host.getId()));
        weeklyB.submitAssignment(host.getId(), UploadAssignment.withLink("https://notion.so/weeklyB/host"));
        weeklyB.submitAssignment(memberC.getId(), UploadAssignment.withLink("https://notion.so/weeklyB/memberC"));

        final StudyWeekly weeklyC = studyWeeklyRepository.save(STUDY_WEEKLY_5.toWeekly(study.getId(), host.getId()));

        final List<WeeklyInformation> result = sut.fetchWeeklyById(study.getId());
        assertThat(result).hasSize(3);

        final WeeklyInformation resultOfWeeklyC = result.get(0);
        assertAll(
                () -> assertThat(resultOfWeeklyC.getId()).isEqualTo(weeklyC.getId()),
                () -> assertThat(resultOfWeeklyC.getTitle()).isEqualTo(weeklyC.getTitle()),
                () -> assertThat(resultOfWeeklyC.getContent()).isEqualTo(weeklyC.getContent()),
                () -> assertThat(resultOfWeeklyC.getWeek()).isEqualTo(weeklyC.getWeek()),
                () -> assertThat(resultOfWeeklyC.isAssignmentExists()).isEqualTo(weeklyC.isAssignmentExists()),
                () -> assertThat(resultOfWeeklyC.isAutoAttendance()).isEqualTo(weeklyC.isAutoAttendance()),
                () -> assertThat(resultOfWeeklyC.getCreator().id()).isEqualTo(host.getId()),
                () -> assertThat(resultOfWeeklyC.getCreator().nickname()).isEqualTo(host.getNickname().getValue()),
                () -> assertThat(resultOfWeeklyC.getAttachments())
                        .map(WeeklyInformation.WeeklyAttachment::link)
                        .containsExactlyInAnyOrderElementsOf(
                                STUDY_WEEKLY_5.getAttachments()
                                        .stream()
                                        .map(UploadAttachment::getLink)
                                        .toList()
                        ),
                () -> assertThat(resultOfWeeklyC.getSubmits()).isEmpty(),
                () -> assertThat(resultOfWeeklyC.getSubmits()).isEmpty()
        );

        final WeeklyInformation resultOfWeeklyB = result.get(1);
        assertAll(
                () -> assertThat(resultOfWeeklyB.getId()).isEqualTo(weeklyB.getId()),
                () -> assertThat(resultOfWeeklyB.getTitle()).isEqualTo(weeklyB.getTitle()),
                () -> assertThat(resultOfWeeklyB.getContent()).isEqualTo(weeklyB.getContent()),
                () -> assertThat(resultOfWeeklyB.getWeek()).isEqualTo(weeklyB.getWeek()),
                () -> assertThat(resultOfWeeklyB.isAssignmentExists()).isEqualTo(weeklyB.isAssignmentExists()),
                () -> assertThat(resultOfWeeklyB.isAutoAttendance()).isEqualTo(weeklyB.isAutoAttendance()),
                () -> assertThat(resultOfWeeklyB.getCreator().id()).isEqualTo(host.getId()),
                () -> assertThat(resultOfWeeklyB.getCreator().nickname()).isEqualTo(host.getNickname().getValue()),
                () -> assertThat(resultOfWeeklyB.getAttachments())
                        .map(WeeklyInformation.WeeklyAttachment::link)
                        .containsExactlyInAnyOrderElementsOf(
                                STUDY_WEEKLY_2.getAttachments()
                                        .stream()
                                        .map(UploadAttachment::getLink)
                                        .toList()
                        ),
                () -> assertThat(resultOfWeeklyB.getSubmits())
                        .map(WeeklyInformation.WeeklySubmit::participant)
                        .map(StudyMember::id)
                        .containsExactlyInAnyOrder(host.getId(), memberC.getId()),
                () -> assertThat(resultOfWeeklyB.getSubmits())
                        .map(WeeklyInformation.WeeklySubmit::submitLink)
                        .containsExactlyInAnyOrder(
                                "https://notion.so/weeklyB/host",
                                "https://notion.so/weeklyB/memberC"
                        )
        );

        final WeeklyInformation resultOfWeeklyA = result.get(2);
        assertAll(
                () -> assertThat(resultOfWeeklyA.getId()).isEqualTo(weeklyA.getId()),
                () -> assertThat(resultOfWeeklyA.getTitle()).isEqualTo(weeklyA.getTitle()),
                () -> assertThat(resultOfWeeklyA.getContent()).isEqualTo(weeklyA.getContent()),
                () -> assertThat(resultOfWeeklyA.getWeek()).isEqualTo(weeklyA.getWeek()),
                () -> assertThat(resultOfWeeklyA.isAssignmentExists()).isEqualTo(weeklyA.isAssignmentExists()),
                () -> assertThat(resultOfWeeklyA.isAutoAttendance()).isEqualTo(weeklyA.isAutoAttendance()),
                () -> assertThat(resultOfWeeklyA.getCreator().id()).isEqualTo(host.getId()),
                () -> assertThat(resultOfWeeklyA.getCreator().nickname()).isEqualTo(host.getNickname().getValue()),
                () -> assertThat(resultOfWeeklyA.getAttachments())
                        .map(WeeklyInformation.WeeklyAttachment::link)
                        .containsExactlyInAnyOrderElementsOf(
                                STUDY_WEEKLY_1.getAttachments()
                                        .stream()
                                        .map(UploadAttachment::getLink)
                                        .toList()
                        ),
                () -> assertThat(resultOfWeeklyA.getSubmits())
                        .map(WeeklyInformation.WeeklySubmit::participant)
                        .map(StudyMember::id)
                        .containsExactlyInAnyOrder(memberA.getId(), memberB.getId(), memberC.getId()),
                () -> assertThat(resultOfWeeklyA.getSubmits())
                        .map(WeeklyInformation.WeeklySubmit::submitLink)
                        .containsExactlyInAnyOrder(
                                "https://notion.so/weeklyA/memberA",
                                "https://notion.so/weeklyA/memberB",
                                "https://notion.so/weeklyA/memberC"
                        )
        );
    }
}
