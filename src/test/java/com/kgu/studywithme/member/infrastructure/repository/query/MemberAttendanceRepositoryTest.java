package com.kgu.studywithme.member.infrastructure.repository.query;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.response.AttendanceRatio;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.response.StudyParticipateWeeks;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.study.domain.attendance.AttendanceStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

import static com.kgu.studywithme.fixture.MemberFixture.*;
import static com.kgu.studywithme.fixture.StudyFixture.JPA;
import static com.kgu.studywithme.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.study.domain.attendance.AttendanceStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member -> MemberAttendanceRepository 테스트")
class MemberAttendanceRepositoryTest extends RepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StudyRepository studyRepository;

    private Member host;
    private final Member[] participants = new Member[5];
    private final Study[] studies = new Study[2];

    @BeforeEach
    void setUp() {
        host = memberRepository.save(JIWON.toMember());
        participants[0] = memberRepository.save(GHOST.toMember());
        participants[1] = memberRepository.save(DUMMY1.toMember());
        participants[2] = memberRepository.save(DUMMY2.toMember());
        participants[3] = memberRepository.save(DUMMY3.toMember());
        participants[4] = memberRepository.save(DUMMY4.toMember());

        studies[0] = studyRepository.save(SPRING.toOnlineStudy(host));
        studies[0].applyParticipation(participants[0]);
        studies[0].approveParticipation(participants[0]);

        studies[1] = studyRepository.save(JPA.toOnlineStudy(host));
        studies[1].applyParticipation(participants[0]);
        studies[1].approveParticipation(participants[0]);
    }

    @Test
    @DisplayName("참여자의 출석률을 조회한다")
    void findAttendanceRatioByMemberId() {
        /* Week 1 */
        studies[0].recordAttendance(host, 1, NON_ATTENDANCE);
        studies[1].recordAttendance(host, 1, ATTENDANCE);
        List<AttendanceRatio> result1 = memberRepository.findAttendanceRatioByMemberId(host.getId());
        assertThat(result1).hasSize(4);
        assertAll(
                () -> assertThat(findCountByStatus(result1, NON_ATTENDANCE)).isEqualTo(1),
                () -> assertThat(findCountByStatus(result1, ATTENDANCE)).isEqualTo(1),
                () -> assertThat(findCountByStatus(result1, LATE)).isEqualTo(0),
                () -> assertThat(findCountByStatus(result1, ABSENCE)).isEqualTo(0)
        );

        /* Week 2 */
        studies[0].recordAttendance(host, 2, ATTENDANCE);
        studies[1].recordAttendance(host, 2, LATE);
        List<AttendanceRatio> result2 = memberRepository.findAttendanceRatioByMemberId(host.getId());
        assertThat(result2).hasSize(4);
        assertAll(
                () -> assertThat(findCountByStatus(result2, NON_ATTENDANCE)).isEqualTo(1),
                () -> assertThat(findCountByStatus(result2, ATTENDANCE)).isEqualTo(2),
                () -> assertThat(findCountByStatus(result2, LATE)).isEqualTo(1),
                () -> assertThat(findCountByStatus(result2, ABSENCE)).isEqualTo(0)
        );

        /* Week 3 */
        studies[0].recordAttendance(host, 3, ATTENDANCE);
        studies[1].recordAttendance(host, 3, ATTENDANCE);
        List<AttendanceRatio> result3 = memberRepository.findAttendanceRatioByMemberId(host.getId());
        assertThat(result3).hasSize(4);
        assertAll(
                () -> assertThat(findCountByStatus(result3, NON_ATTENDANCE)).isEqualTo(1),
                () -> assertThat(findCountByStatus(result3, ATTENDANCE)).isEqualTo(4),
                () -> assertThat(findCountByStatus(result3, LATE)).isEqualTo(1),
                () -> assertThat(findCountByStatus(result3, ABSENCE)).isEqualTo(0)
        );

        /* Week 4 */
        studies[0].recordAttendance(host, 4, LATE);
        studies[1].recordAttendance(host, 4, ABSENCE);
        List<AttendanceRatio> result4 = memberRepository.findAttendanceRatioByMemberId(host.getId());
        assertThat(result4).hasSize(4);
        assertAll(
                () -> assertThat(findCountByStatus(result4, NON_ATTENDANCE)).isEqualTo(1),
                () -> assertThat(findCountByStatus(result4, ATTENDANCE)).isEqualTo(4),
                () -> assertThat(findCountByStatus(result4, LATE)).isEqualTo(2),
                () -> assertThat(findCountByStatus(result4, ABSENCE)).isEqualTo(1)
        );

        /* Week 5 */
        studies[0].recordAttendance(host, 5, ABSENCE);
        studies[1].recordAttendance(host, 5, NON_ATTENDANCE);
        List<AttendanceRatio> result5 = memberRepository.findAttendanceRatioByMemberId(host.getId());
        assertThat(result5).hasSize(4);
        assertAll(
                () -> assertThat(findCountByStatus(result5, NON_ATTENDANCE)).isEqualTo(2),
                () -> assertThat(findCountByStatus(result5, ATTENDANCE)).isEqualTo(4),
                () -> assertThat(findCountByStatus(result5, LATE)).isEqualTo(2),
                () -> assertThat(findCountByStatus(result5, ABSENCE)).isEqualTo(2)
        );
    }

    @Test
    @DisplayName("참여자 PK를 통해서 [스터디 PK + 해당 스터디의 출석 정보]를 조회한다")
    void findParticipationWeekByMemberId() {
        // given
        applyAttendance(studies[0], host, List.of(1, 2, 3, 4, 5, 6));
        applyAttendance(studies[1], host, List.of(4, 5, 6));

        applyAttendance(studies[0], participants[0], List.of(3, 4, 5));
        applyAttendance(studies[1], participants[0], List.of(1, 2, 3, 4));

        // when - then
        List<StudyParticipateWeeks> hostMetadata = memberRepository.findParticipateWeeksInStudyByMemberId(host.getId());
        assertThatParticipationMetadataMatch(
                hostMetadata,
                List.of(
                        studies[0], studies[0], studies[0], studies[0], studies[0], studies[0],
                        studies[1], studies[1], studies[1]
                ),
                List.of(
                        1, 2, 3, 4, 5, 6,
                        4, 5, 6
                )
        );

        List<StudyParticipateWeeks> participantMetadata = memberRepository.findParticipateWeeksInStudyByMemberId(participants[0].getId());
        assertThatParticipationMetadataMatch(
                participantMetadata,
                List.of(
                        studies[0], studies[0], studies[0],
                        studies[1], studies[1], studies[1], studies[1]
                ),
                List.of(
                        3, 4, 5,
                        1, 2, 3, 4
                )
        );
    }

    @Test
    @DisplayName("참여자의 출석 현황을 조회한다")
    void getAttendanceCount() {
        /* 미출석 1회 */
        studies[0].recordAttendance(host, 1, NON_ATTENDANCE);
        assertAll(
                () -> assertThat(memberRepository.getAttendanceCount(studies[0].getId(), host.getId(), NON_ATTENDANCE)).isEqualTo(1),
                () -> assertThat(memberRepository.getAttendanceCount(studies[0].getId(), host.getId(), ATTENDANCE)).isEqualTo(0),
                () -> assertThat(memberRepository.getAttendanceCount(studies[0].getId(), host.getId(), LATE)).isEqualTo(0),
                () -> assertThat(memberRepository.getAttendanceCount(studies[0].getId(), host.getId(), ABSENCE)).isEqualTo(0)
        );

        /* 미출석 1회 + 출석 1회 */
        studies[0].recordAttendance(host, 2, ATTENDANCE);
        assertAll(
                () -> assertThat(memberRepository.getAttendanceCount(studies[0].getId(), host.getId(), NON_ATTENDANCE)).isEqualTo(1),
                () -> assertThat(memberRepository.getAttendanceCount(studies[0].getId(), host.getId(), ATTENDANCE)).isEqualTo(1),
                () -> assertThat(memberRepository.getAttendanceCount(studies[0].getId(), host.getId(), LATE)).isEqualTo(0),
                () -> assertThat(memberRepository.getAttendanceCount(studies[0].getId(), host.getId(), ABSENCE)).isEqualTo(0)
        );

        /* 미출석 1회 + 출석 2회 */
        studies[0].recordAttendance(host, 3, ATTENDANCE);
        assertAll(
                () -> assertThat(memberRepository.getAttendanceCount(studies[0].getId(), host.getId(), NON_ATTENDANCE)).isEqualTo(1),
                () -> assertThat(memberRepository.getAttendanceCount(studies[0].getId(), host.getId(), ATTENDANCE)).isEqualTo(2),
                () -> assertThat(memberRepository.getAttendanceCount(studies[0].getId(), host.getId(), LATE)).isEqualTo(0),
                () -> assertThat(memberRepository.getAttendanceCount(studies[0].getId(), host.getId(), ABSENCE)).isEqualTo(0)
        );

        /* 미출석 1회 + 출석 2회 + 지각 1회 */
        studies[0].recordAttendance(host, 4, LATE);
        assertAll(
                () -> assertThat(memberRepository.getAttendanceCount(studies[0].getId(), host.getId(), NON_ATTENDANCE)).isEqualTo(1),
                () -> assertThat(memberRepository.getAttendanceCount(studies[0].getId(), host.getId(), ATTENDANCE)).isEqualTo(2),
                () -> assertThat(memberRepository.getAttendanceCount(studies[0].getId(), host.getId(), LATE)).isEqualTo(1),
                () -> assertThat(memberRepository.getAttendanceCount(studies[0].getId(), host.getId(), ABSENCE)).isEqualTo(0)
        );

        /* 미출석 1회 + 출석 2회 + 지각 1회 + 결석 1회 */
        studies[0].recordAttendance(host, 5, ABSENCE);
        assertAll(
                () -> assertThat(memberRepository.getAttendanceCount(studies[0].getId(), host.getId(), NON_ATTENDANCE)).isEqualTo(1),
                () -> assertThat(memberRepository.getAttendanceCount(studies[0].getId(), host.getId(), ATTENDANCE)).isEqualTo(2),
                () -> assertThat(memberRepository.getAttendanceCount(studies[0].getId(), host.getId(), LATE)).isEqualTo(1),
                () -> assertThat(memberRepository.getAttendanceCount(studies[0].getId(), host.getId(), ABSENCE)).isEqualTo(1)
        );
    }

    @Test
    @DisplayName("결석한 참여자들의 Score를 일괄 업데이트한다 [For Scheduling]")
    void applyAbsenceScore() {
        // given
        final Set<Long> absenceParticipantIds = Set.of(
                participants[2].getId(),
                participants[3].getId()
        );
        System.out.println("id = " + absenceParticipantIds);

        // when
        memberRepository.applyScoreToAbsenceParticipant(absenceParticipantIds);

        // then
        List<Member> members = memberRepository.findAll();
        List<Integer> expectScores = List.of(
                80, // host
                80, // participants[0]
                80, // participants[1]
                80 - 5, // participants[2]
                80 - 5, // participants[3]
                80 // participants[4]
        );

        for (int i = 0; i < expectScores.size(); i++) {
            Member member = members.get(i);
            int expectScore = expectScores.get(i);

            assertThat(member.getScore()).isEqualTo(expectScore);
        }
    }

    private int findCountByStatus(List<AttendanceRatio> attendanceRatios, AttendanceStatus status) {
        return attendanceRatios.stream()
                .filter(ratio -> ratio.status() == status)
                .findFirst()
                .map(AttendanceRatio::count)
                .orElse(0);
    }

    private void applyAttendance(Study study, Member member, List<Integer> weeks) {
        weeks.forEach(week -> study.recordAttendance(member, week, ATTENDANCE));
    }

    private void assertThatParticipationMetadataMatch(
            List<StudyParticipateWeeks> metadata,
            List<Study> studies,
            List<Integer> weeks
    ) {
        int totalSize = weeks.size();
        assertThat(metadata).hasSize(totalSize);

        for (int i = 0; i < totalSize; i++) {
            StudyParticipateWeeks specificMetadata = metadata.get(i);
            Study study = studies.get(i);
            int week = weeks.get(i);

            assertAll(
                    () -> assertThat(specificMetadata.studyId()).isEqualTo(study.getId()),
                    () -> assertThat(specificMetadata.week()).isEqualTo(week)
            );
        }
    }
}
