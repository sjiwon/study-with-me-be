package com.kgu.studywithme.study.infrastructure.repository.query;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.study.infrastructure.repository.query.dto.*;
import com.kgu.studywithme.studyattendance.domain.StudyAttendance;
import com.kgu.studywithme.studyattendance.domain.StudyAttendanceRepository;
import com.kgu.studywithme.studynotice.domain.StudyNotice;
import com.kgu.studywithme.studynotice.domain.StudyNoticeRepository;
import com.kgu.studywithme.studyparticipant.domain.StudyParticipant;
import com.kgu.studywithme.studyparticipant.domain.StudyParticipantRepository;
import com.kgu.studywithme.studyreview.domain.StudyReview;
import com.kgu.studywithme.studyreview.domain.StudyReviewRepository;
import com.kgu.studywithme.studyweekly.domain.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.StudyWeeklyRepository;
import com.kgu.studywithme.studyweekly.domain.attachment.UploadAttachment;
import com.kgu.studywithme.studyweekly.domain.submit.UploadAssignment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static com.kgu.studywithme.fixture.MemberFixture.*;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.fixture.StudyWeeklyFixture.*;
import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.*;
import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.APPROVE;
import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.GRADUATED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Study -> StudyInformationQueryRepository 테스트")
class StudyInformationQueryRepositoryTest extends RepositoryTest {
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

        study = studyRepository.save(SPRING.toOnlineStudy(host.getId()));
    }

    @Test
    @DisplayName("스터디 기본 정보를 조회한다")
    void fetchBasicInformationById() {
        /* host, memberA, memberB 참여 승인 & memberC 신청 대기 */
        studyParticipantRepository.saveAll(
                List.of(
                        StudyParticipant.applyHost(study.getId(), host.getId()),
                        StudyParticipant.applyParticipant(study.getId(), memberA.getId(), APPROVE),
                        StudyParticipant.applyParticipant(study.getId(), memberB.getId(), APPROVE),
                        StudyParticipant.applyInStudy(study.getId(), memberC.getId())
                )
        );

        final StudyBasicInformation result = studyRepository.fetchBasicInformationById(study.getId());
        assertAll(
                () -> assertThat(result.getId()).isEqualTo(study.getId()),
                () -> assertThat(result.getName()).isEqualTo(study.getNameValue()),
                () -> assertThat(result.getDescription()).isEqualTo(study.getDescriptionValue()),
                () -> assertThat(result.getCategory()).isEqualTo(study.getCategory().getName()),
                () -> assertThat(result.getThumbnail().name()).isEqualTo(study.getThumbnail().getImageName()),
                () -> assertThat(result.getThumbnail().background()).isEqualTo(study.getThumbnail().getBackground()),
                () -> assertThat(result.getType()).isEqualTo(study.getType().getDescription()),
                () -> assertThat(result.getLocation()).isEqualTo(study.getLocation()),
                () -> assertThat(result.getRecruitmentStatus()).isEqualTo(study.getRecruitmentStatus().getDescription()),
                () -> assertThat(result.getMaxMember()).isEqualTo(study.getCapacity()),
                () -> assertThat(result.getMinimumAttendanceForGraduation()).isEqualTo(study.getMinimumAttendanceForGraduation()),
                () -> assertThat(result.getRemainingOpportunityToUpdateGraduationPolicy()).isEqualTo(study.getGraduationPolicy().getUpdateChance()),
                () -> assertThat(result.getHost().id()).isEqualTo(host.getId()),
                () -> assertThat(result.getHost().nickname()).isEqualTo(host.getNicknameValue()),
                () -> assertThat(result.getCurrentMemberCount()).isEqualTo(3), // host, memberA, memberB
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
        studyParticipantRepository.saveAll(
                List.of(
                        StudyParticipant.applyHost(study.getId(), host.getId()),
                        StudyParticipant.applyParticipant(study.getId(), memberA.getId(), GRADUATED),
                        StudyParticipant.applyParticipant(study.getId(), memberB.getId(), GRADUATED),
                        StudyParticipant.applyParticipant(study.getId(), memberC.getId(), GRADUATED)
                )
        );

        /* 리뷰 2건 */
        studyReviewRepository.saveAll(
                List.of(
                        StudyReview.writeReview(study.getId(), memberA.getId(), "Good Study"),
                        StudyReview.writeReview(study.getId(), memberB.getId(), "Good Study")
                )
        );

        final ReviewInformation result1 = studyRepository.fetchReviewById(study.getId());
        assertAll(
                () -> assertThat(result1.reviews()).hasSize(2),
                () -> assertThat(result1.reviews())
                        .map(ReviewInformation.ReviewMetadata::reviewer)
                        .map(StudyMember::id)
                        .containsExactly(memberB.getId(), memberA.getId()),
                () -> assertThat(result1.graduateCount()).isEqualTo(3)
        );

        /* 리뷰 추가 1건 */
        studyReviewRepository.save(
                StudyReview.writeReview(study.getId(), memberC.getId(), "Good Study")
        );

        final ReviewInformation result2 = studyRepository.fetchReviewById(study.getId());
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
        studyParticipantRepository.saveAll(
                List.of(
                        StudyParticipant.applyHost(study.getId(), host.getId()),
                        StudyParticipant.applyInStudy(study.getId(), memberA.getId()),
                        StudyParticipant.applyInStudy(study.getId(), memberB.getId()),
                        StudyParticipant.applyInStudy(study.getId(), memberC.getId())
                )
        );

        final StudyParticipantInformation result1 = studyRepository.fetchParticipantById(study.getId());
        assertAll(
                () -> assertThat(result1.host().id()).isEqualTo(host.getId()),
                () -> assertThat(result1.participants()).isEmpty()
        );

        /* memberA, memberC 참여 승인 */
        studyParticipantRepository.updateParticipantStatus(study.getId(), memberA.getId(), APPROVE);
        studyParticipantRepository.updateParticipantStatus(study.getId(), memberC.getId(), APPROVE);

        final StudyParticipantInformation result2 = studyRepository.fetchParticipantById(study.getId());
        assertAll(
                () -> assertThat(result2.host().id()).isEqualTo(host.getId()),
                () -> assertThat(result2.participants())
                        .map(StudyMember::id)
                        .containsExactlyInAnyOrder(memberA.getId(), memberC.getId())
        );

        /* memberB 참여 승인 */
        studyParticipantRepository.updateParticipantStatus(study.getId(), memberB.getId(), APPROVE);

        final StudyParticipantInformation result3 = studyRepository.fetchParticipantById(study.getId());
        assertAll(
                () -> assertThat(result3.host().id()).isEqualTo(host.getId()),
                () -> assertThat(result3.participants())
                        .map(StudyMember::id)
                        .containsExactlyInAnyOrder(memberA.getId(), memberB.getId(), memberC.getId())
        );

        /* memberC 졸업 */
        studyParticipantRepository.updateParticipantStatus(study.getId(), memberC.getId(), GRADUATED);

        final StudyParticipantInformation result4 = studyRepository.fetchParticipantById(study.getId());
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
        studyParticipantRepository.saveAll(
                List.of(
                        StudyParticipant.applyHost(study.getId(), host.getId())
                                .apply(1L, LocalDateTime.now().minusDays(4)),
                        StudyParticipant.applyInStudy(study.getId(), memberA.getId())
                                .apply(2L, LocalDateTime.now().minusDays(3)),
                        StudyParticipant.applyInStudy(study.getId(), memberB.getId())
                                .apply(3L, LocalDateTime.now().minusDays(2)),
                        StudyParticipant.applyInStudy(study.getId(), memberC.getId())
                                .apply(4L, LocalDateTime.now().minusDays(1))
                )
        );

        final List<StudyApplicantInformation> result1 = studyRepository.fetchApplicantById(study.getId());
        assertAll(
                () -> assertThat(result1).hasSize(3),
                () -> assertThat(result1)
                        .map(StudyApplicantInformation::id)
                        .containsExactly(memberC.getId(), memberB.getId(), memberA.getId())
        );

        /* memberA, memberC 참여 승인 */
        studyParticipantRepository.updateParticipantStatus(study.getId(), memberA.getId(), APPROVE);
        studyParticipantRepository.updateParticipantStatus(study.getId(), memberC.getId(), APPROVE);

        final List<StudyApplicantInformation> result2 = studyRepository.fetchApplicantById(study.getId());
        assertAll(
                () -> assertThat(result2).hasSize(1),
                () -> assertThat(result2)
                        .map(StudyApplicantInformation::id)
                        .containsExactly(memberB.getId())
        );

        /* memberB 참여 승인 */
        studyParticipantRepository.updateParticipantStatus(study.getId(), memberB.getId(), APPROVE);

        final List<StudyApplicantInformation> result3 = studyRepository.fetchApplicantById(study.getId());
        assertThat(result3).isEmpty();
    }

    @Test
    @DisplayName("스터디 공지사항을 조회한다")
    void fetchNoticeById() {
        /* 공지사항 2건 */
        final StudyNotice notice1 = studyNoticeRepository.save(StudyNotice.writeNotice(
                study.getId(),
                host.getId(),
                "Notice 1",
                "Notice 1 Content"
        ));
        notice1.addComment(memberA.getId(), "OK");
        notice1.addComment(memberB.getId(), "OK");
        notice1.addComment(memberC.getId(), "OK");

        final StudyNotice notice2 = studyNoticeRepository.save(StudyNotice.writeNotice(
                study.getId(),
                host.getId(),
                "Notice 2",
                "Notice 2 Content"
        ));
        notice2.addComment(memberA.getId(), "OK");
        notice2.addComment(memberC.getId(), "OK");

        final List<NoticeInformation> result = studyRepository.fetchNoticeById(study.getId());
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
                        .containsExactly(memberC.getId(), memberA.getId()),

                // Weely1 Comments
                () -> assertThat(result.get(1).getComments()).hasSize(3),
                () -> assertThat(result.get(1).getComments())
                        .map(NoticeInformation.CommentInformation::writer)
                        .map(StudyMember::id)
                        .containsExactly(memberC.getId(), memberB.getId(), memberA.getId())
        );
    }

    @Test
    @DisplayName("스터디 참여자들의 출석 정보를 조회한다 [Only Approved Participant]")
    void fetchAttendanceById() {
        /* host, memberA, memberC 참여 승인 & memberB 신청 대기 */
        studyParticipantRepository.saveAll(
                List.of(
                        StudyParticipant.applyHost(study.getId(), host.getId())
                                .apply(1L, LocalDateTime.now().minusDays(4)),
                        StudyParticipant.applyParticipant(study.getId(), memberA.getId(), APPROVE)
                                .apply(2L, LocalDateTime.now().minusDays(3)),
                        StudyParticipant.applyInStudy(study.getId(), memberB.getId())
                                .apply(3L, LocalDateTime.now().minusDays(2)),
                        StudyParticipant.applyParticipant(study.getId(), memberC.getId(), APPROVE)
                                .apply(4L, LocalDateTime.now().minusDays(1))
                )
        );

        studyAttendanceRepository.saveAll(
                List.of(
                        StudyAttendance.recordAttendance(study.getId(), host.getId(), 1, ATTENDANCE),
                        StudyAttendance.recordAttendance(study.getId(), host.getId(), 2, ATTENDANCE),
                        StudyAttendance.recordAttendance(study.getId(), host.getId(), 3, ATTENDANCE),
                        StudyAttendance.recordAttendance(study.getId(), host.getId(), 4, ATTENDANCE),

                        StudyAttendance.recordAttendance(study.getId(), memberA.getId(), 1, ATTENDANCE),
                        StudyAttendance.recordAttendance(study.getId(), memberA.getId(), 2, LATE),
                        StudyAttendance.recordAttendance(study.getId(), memberA.getId(), 3, ATTENDANCE),
                        StudyAttendance.recordAttendance(study.getId(), memberA.getId(), 4, NON_ATTENDANCE),

                        StudyAttendance.recordAttendance(study.getId(), memberC.getId(), 1, ATTENDANCE),
                        StudyAttendance.recordAttendance(study.getId(), memberC.getId(), 2, ATTENDANCE),
                        StudyAttendance.recordAttendance(study.getId(), memberC.getId(), 3, LATE)
                )
        );

        final List<AttendanceInformation> result = studyRepository.fetchAttendanceById(study.getId());
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
                                ATTENDANCE.getDescription(),
                                ATTENDANCE.getDescription(),
                                ATTENDANCE.getDescription(),
                                ATTENDANCE.getDescription()
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
                                ATTENDANCE.getDescription(),
                                LATE.getDescription(),
                                ATTENDANCE.getDescription(),
                                NON_ATTENDANCE.getDescription()
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
                                ATTENDANCE.getDescription(),
                                ATTENDANCE.getDescription(),
                                LATE.getDescription()
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
        final StudyWeekly weeklyA = studyWeeklyRepository.save(
                STUDY_WEEKLY_1.toWeeklyWithAssignment(study.getId(), host.getId())
        );
        weeklyA.submitAssignment(memberA.getId(), UploadAssignment.withLink("https://notion.so/weeklyA/memberA"));
        weeklyA.submitAssignment(memberB.getId(), UploadAssignment.withLink("https://notion.so/weeklyA/memberB"));
        weeklyA.submitAssignment(memberC.getId(), UploadAssignment.withLink("https://notion.so/weeklyA/memberC"));

        final StudyWeekly weeklyB = studyWeeklyRepository.save(
                STUDY_WEEKLY_2.toWeeklyWithAssignment(study.getId(), host.getId())
        );
        weeklyB.submitAssignment(host.getId(), UploadAssignment.withLink("https://notion.so/weeklyB/host"));
        weeklyB.submitAssignment(memberC.getId(), UploadAssignment.withLink("https://notion.so/weeklyB/memberC"));

        final StudyWeekly weeklyC = studyWeeklyRepository.save(
                STUDY_WEEKLY_5.toWeekly(study.getId(), host.getId())
        );

        final List<WeeklyInformation> result = studyRepository.fetchWeeklyById(study.getId());
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
                () -> assertThat(resultOfWeeklyC.getCreator().nickname()).isEqualTo(host.getNicknameValue()),
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
                () -> assertThat(resultOfWeeklyB.getCreator().nickname()).isEqualTo(host.getNicknameValue()),
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
                () -> assertThat(resultOfWeeklyA.getCreator().nickname()).isEqualTo(host.getNicknameValue()),
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
