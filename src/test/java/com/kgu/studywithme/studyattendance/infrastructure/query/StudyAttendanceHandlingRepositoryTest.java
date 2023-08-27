package com.kgu.studywithme.studyattendance.infrastructure.query;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.studyattendance.domain.StudyAttendance;
import com.kgu.studywithme.studyattendance.domain.StudyAttendanceRepository;
import com.kgu.studywithme.studyattendance.infrastructure.query.dto.NonAttendanceWeekly;
import com.kgu.studywithme.studyattendance.infrastructure.query.dto.StudyAttendanceWeekly;
import com.kgu.studywithme.studyparticipant.domain.StudyParticipant;
import com.kgu.studywithme.studyparticipant.domain.StudyParticipantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.List;

import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY1;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY2;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY3;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY4;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(StudyAttendanceHandlingRepository.class)
@DisplayName("StudyAttendance -> StudyAttendanceHandlingRepository 테스트")
class StudyAttendanceHandlingRepositoryTest extends RepositoryTest {
    @Autowired
    private StudyAttendanceHandlingRepository studyAttendanceHandlingRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private StudyAttendanceRepository studyAttendanceRepository;

    @Autowired
    private StudyParticipantRepository studyParticipantRepository;

    private final Member[] members = new Member[5];
    private final Study[] studies = new Study[5];

    @BeforeEach
    void setUp() {
        members[0] = memberRepository.save(JIWON.toMember());
        members[1] = memberRepository.save(DUMMY1.toMember());
        members[2] = memberRepository.save(DUMMY2.toMember());
        members[3] = memberRepository.save(DUMMY3.toMember());
        members[4] = memberRepository.save(DUMMY4.toMember());

        final Member host = memberRepository.save(GHOST.toMember());
        studies[0] = studyRepository.save(SPRING.toOnlineStudy(host.getId()));
        studies[1] = studyRepository.save(JPA.toOnlineStudy(host.getId()));
        studies[2] = studyRepository.save(KOTLIN.toOnlineStudy(host.getId()));
        studies[3] = studyRepository.save(NETWORK.toOnlineStudy(host.getId()));
        studies[4] = studyRepository.save(EFFECTIVE_JAVA.toOnlineStudy(host.getId()));
    }

    @Test
    @DisplayName("미출석 주차 정보들을 조회한다")
    void findNonAttendanceInformation() {
        /* 1주차 */
        studyAttendanceRepository.saveAll(
                List.of(
                        StudyAttendance.recordAttendance(studies[0].getId(), members[0].getId(), 1, NON_ATTENDANCE),
                        StudyAttendance.recordAttendance(studies[0].getId(), members[1].getId(), 1, NON_ATTENDANCE),
                        StudyAttendance.recordAttendance(studies[0].getId(), members[2].getId(), 1, ATTENDANCE),
                        StudyAttendance.recordAttendance(studies[0].getId(), members[3].getId(), 1, ATTENDANCE),
                        StudyAttendance.recordAttendance(studies[0].getId(), members[4].getId(), 1, NON_ATTENDANCE)
                )
        );

        final List<NonAttendanceWeekly> result1 = studyAttendanceHandlingRepository.findNonAttendanceInformation();
        assertAll(
                () -> assertThat(result1)
                        .map(NonAttendanceWeekly::studyId)
                        .containsExactly(studies[0].getId(), studies[0].getId(), studies[0].getId()),
                () -> assertThat(result1)
                        .map(NonAttendanceWeekly::week)
                        .containsExactly(1, 1, 1),
                () -> assertThat(result1)
                        .map(NonAttendanceWeekly::participantId)
                        .containsExactly(members[0].getId(), members[1].getId(), members[4].getId())
        );

        /* 2주차 */
        studyAttendanceRepository.saveAll(
                List.of(
                        StudyAttendance.recordAttendance(studies[0].getId(), members[0].getId(), 2, ATTENDANCE),
                        StudyAttendance.recordAttendance(studies[0].getId(), members[1].getId(), 2, NON_ATTENDANCE),
                        StudyAttendance.recordAttendance(studies[0].getId(), members[2].getId(), 2, ATTENDANCE),
                        StudyAttendance.recordAttendance(studies[0].getId(), members[3].getId(), 2, NON_ATTENDANCE),
                        StudyAttendance.recordAttendance(studies[0].getId(), members[4].getId(), 2, ATTENDANCE)
                )
        );

        final List<NonAttendanceWeekly> result2 = studyAttendanceHandlingRepository.findNonAttendanceInformation();
        assertAll(
                () -> assertThat(result2)
                        .map(NonAttendanceWeekly::studyId)
                        .containsExactly(
                                studies[0].getId(), studies[0].getId(), studies[0].getId(),
                                studies[0].getId(), studies[0].getId()
                        ),
                () -> assertThat(result2)
                        .map(NonAttendanceWeekly::week)
                        .containsExactly(
                                1, 1, 1,
                                2, 2
                        ),
                () -> assertThat(result2)
                        .map(NonAttendanceWeekly::participantId)
                        .containsExactly(
                                members[0].getId(), members[1].getId(), members[4].getId(),
                                members[1].getId(), members[3].getId()
                        )
        );
    }

    @Test
    @DisplayName("사용자가 출석을 진행한 모든 스터디의 주차를 조회한다")
    void findParticipateWeeksInStudyByMemberId() {
        /* 모든 스터디 참여 */
        studyParticipantRepository.saveAll(
                List.of(
                        StudyParticipant.applyParticipant(studies[0].getId(), members[0].getId(), APPROVE),
                        StudyParticipant.applyParticipant(studies[1].getId(), members[0].getId(), APPROVE),
                        StudyParticipant.applyParticipant(studies[2].getId(), members[0].getId(), APPROVE),
                        StudyParticipant.applyParticipant(studies[3].getId(), members[0].getId(), APPROVE),
                        StudyParticipant.applyParticipant(studies[4].getId(), members[0].getId(), APPROVE)
                )
        );

        /* Week 1 */
        studyAttendanceRepository.saveAll(
                List.of(
                        StudyAttendance.recordAttendance(studies[0].getId(), members[0].getId(), 1, ATTENDANCE),
                        StudyAttendance.recordAttendance(studies[1].getId(), members[0].getId(), 1, ATTENDANCE),
                        StudyAttendance.recordAttendance(studies[2].getId(), members[0].getId(), 1, LATE),
                        StudyAttendance.recordAttendance(studies[3].getId(), members[0].getId(), 1, ATTENDANCE),
                        StudyAttendance.recordAttendance(studies[4].getId(), members[0].getId(), 1, ABSENCE)
                )
        );

        final List<StudyAttendanceWeekly> participateWeeks1 = studyAttendanceHandlingRepository.findParticipateWeeksInStudyByMemberId(members[0].getId());
        assertAll(
                () -> assertThat(groupingWeeksByStudyId(participateWeeks1, studies[0].getId())).containsExactlyInAnyOrder(1),
                () -> assertThat(groupingWeeksByStudyId(participateWeeks1, studies[1].getId())).containsExactlyInAnyOrder(1),
                () -> assertThat(groupingWeeksByStudyId(participateWeeks1, studies[2].getId())).containsExactlyInAnyOrder(1),
                () -> assertThat(groupingWeeksByStudyId(participateWeeks1, studies[3].getId())).containsExactlyInAnyOrder(1),
                () -> assertThat(groupingWeeksByStudyId(participateWeeks1, studies[4].getId())).containsExactlyInAnyOrder(1)
        );

        /* Week 2 */
        studyAttendanceRepository.saveAll(
                List.of(
                        StudyAttendance.recordAttendance(studies[0].getId(), members[0].getId(), 2, ATTENDANCE),
                        StudyAttendance.recordAttendance(studies[3].getId(), members[0].getId(), 2, ATTENDANCE),
                        StudyAttendance.recordAttendance(studies[4].getId(), members[0].getId(), 2, ABSENCE)
                )
        );

        final List<StudyAttendanceWeekly> participateWeeks2 = studyAttendanceHandlingRepository.findParticipateWeeksInStudyByMemberId(members[0].getId());
        assertAll(
                () -> assertThat(groupingWeeksByStudyId(participateWeeks2, studies[0].getId())).containsExactlyInAnyOrder(1, 2),
                () -> assertThat(groupingWeeksByStudyId(participateWeeks2, studies[1].getId())).containsExactlyInAnyOrder(1),
                () -> assertThat(groupingWeeksByStudyId(participateWeeks2, studies[2].getId())).containsExactlyInAnyOrder(1),
                () -> assertThat(groupingWeeksByStudyId(participateWeeks2, studies[3].getId())).containsExactlyInAnyOrder(1, 2),
                () -> assertThat(groupingWeeksByStudyId(participateWeeks2, studies[4].getId())).containsExactlyInAnyOrder(1, 2)
        );

        /* Week 3 */
        studyAttendanceRepository.save(StudyAttendance.recordAttendance(studies[0].getId(), members[0].getId(), 3, ATTENDANCE));

        final List<StudyAttendanceWeekly> participateWeeks3 = studyAttendanceHandlingRepository.findParticipateWeeksInStudyByMemberId(members[0].getId());
        assertAll(
                () -> assertThat(groupingWeeksByStudyId(participateWeeks3, studies[0].getId())).containsExactlyInAnyOrder(1, 2, 3),
                () -> assertThat(groupingWeeksByStudyId(participateWeeks3, studies[1].getId())).containsExactlyInAnyOrder(1),
                () -> assertThat(groupingWeeksByStudyId(participateWeeks3, studies[2].getId())).containsExactlyInAnyOrder(1),
                () -> assertThat(groupingWeeksByStudyId(participateWeeks2, studies[3].getId())).containsExactlyInAnyOrder(1, 2),
                () -> assertThat(groupingWeeksByStudyId(participateWeeks3, studies[4].getId())).containsExactlyInAnyOrder(1, 2)
        );
    }

    private List<Integer> groupingWeeksByStudyId(
            final List<StudyAttendanceWeekly> participateWeeks,
            final Long studyId
    ) {
        return participateWeeks.stream()
                .filter(participateWeek -> participateWeek.studyId().equals(studyId))
                .map(StudyAttendanceWeekly::week)
                .toList();
    }


    @Test
    @DisplayName("사용자의 스터디 출석 횟수를 조회한다")
    void getAttendanceCount() {
        /* 출석 1회 */
        studyAttendanceRepository.save(
                StudyAttendance.recordAttendance(studies[0].getId(), members[0].getId(), 1, ATTENDANCE)
        );
        assertThat(studyAttendanceHandlingRepository.getAttendanceCount(studies[0].getId(), members[0].getId())).isEqualTo(1);

        /* 출석 1회 + 지각 1회 */
        studyAttendanceRepository.save(
                StudyAttendance.recordAttendance(studies[0].getId(), members[0].getId(), 2, LATE)
        );
        assertThat(studyAttendanceHandlingRepository.getAttendanceCount(studies[0].getId(), members[0].getId())).isEqualTo(1);

        /* 출석 2회 + 지각 1회 */
        studyAttendanceRepository.save(
                StudyAttendance.recordAttendance(studies[0].getId(), members[0].getId(), 3, ATTENDANCE)
        );
        assertThat(studyAttendanceHandlingRepository.getAttendanceCount(studies[0].getId(), members[0].getId())).isEqualTo(2);
    }
}
