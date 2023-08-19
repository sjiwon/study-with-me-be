package com.kgu.studywithme.member.infrastructure.query;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.infrastructure.persistence.MemberJpaRepository;
import com.kgu.studywithme.member.infrastructure.query.dto.StudyParticipateWeeks;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.infrastructure.persistence.StudyJpaRepository;
import com.kgu.studywithme.studyattendance.domain.StudyAttendance;
import com.kgu.studywithme.studyattendance.infrastructure.persistence.StudyAttendanceJpaRepository;
import com.kgu.studywithme.studyparticipant.domain.StudyParticipant;
import com.kgu.studywithme.studyparticipant.infrastructure.persistence.StudyParticipantJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.List;

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
import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.APPROVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(MemberAttendanceRepository.class)
@DisplayName("Member -> MemberAttendanceRepository 테스트")
class MemberAttendanceRepositoryTest extends RepositoryTest {
    @Autowired
    private MemberAttendanceRepository memberAttendanceRepository;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private StudyJpaRepository studyJpaRepository;

    @Autowired
    private StudyParticipantJpaRepository studyParticipantJpaRepository;

    @Autowired
    private StudyAttendanceJpaRepository studyAttendanceJpaRepository;

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
    @DisplayName("사용자가 출석을 진행한 모든 스터디의 주차를 조회한다")
    void findParticipateWeeksInStudyByMemberId() {
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

        final List<StudyParticipateWeeks> participateWeeks1 = memberAttendanceRepository.findParticipateWeeksInStudyByMemberId(member.getId());
        assertAll(
                () -> assertThat(groupingWeeksByStudyId(participateWeeks1, studyA.getId())).containsExactlyInAnyOrder(1),
                () -> assertThat(groupingWeeksByStudyId(participateWeeks1, studyB.getId())).containsExactlyInAnyOrder(1),
                () -> assertThat(groupingWeeksByStudyId(participateWeeks1, studyC.getId())).containsExactlyInAnyOrder(1),
                () -> assertThat(groupingWeeksByStudyId(participateWeeks1, studyD.getId())).containsExactlyInAnyOrder(1),
                () -> assertThat(groupingWeeksByStudyId(participateWeeks1, studyE.getId())).containsExactlyInAnyOrder(1)
        );

        /* Week 2 */
        studyAttendanceJpaRepository.saveAll(
                List.of(
                        StudyAttendance.recordAttendance(studyA.getId(), member.getId(), 2, ATTENDANCE),
                        StudyAttendance.recordAttendance(studyD.getId(), member.getId(), 2, ATTENDANCE),
                        StudyAttendance.recordAttendance(studyE.getId(), member.getId(), 2, ABSENCE)
                )
        );

        final List<StudyParticipateWeeks> participateWeeks2 = memberAttendanceRepository.findParticipateWeeksInStudyByMemberId(member.getId());
        assertAll(
                () -> assertThat(groupingWeeksByStudyId(participateWeeks2, studyA.getId())).containsExactlyInAnyOrder(1, 2),
                () -> assertThat(groupingWeeksByStudyId(participateWeeks2, studyB.getId())).containsExactlyInAnyOrder(1),
                () -> assertThat(groupingWeeksByStudyId(participateWeeks2, studyC.getId())).containsExactlyInAnyOrder(1),
                () -> assertThat(groupingWeeksByStudyId(participateWeeks2, studyD.getId())).containsExactlyInAnyOrder(1, 2),
                () -> assertThat(groupingWeeksByStudyId(participateWeeks2, studyE.getId())).containsExactlyInAnyOrder(1, 2)
        );

        /* Week 3 */
        studyAttendanceJpaRepository.save(StudyAttendance.recordAttendance(studyA.getId(), member.getId(), 3, ATTENDANCE));

        final List<StudyParticipateWeeks> participateWeeks3 = memberAttendanceRepository.findParticipateWeeksInStudyByMemberId(member.getId());
        assertAll(
                () -> assertThat(groupingWeeksByStudyId(participateWeeks3, studyA.getId())).containsExactlyInAnyOrder(1, 2, 3),
                () -> assertThat(groupingWeeksByStudyId(participateWeeks3, studyB.getId())).containsExactlyInAnyOrder(1),
                () -> assertThat(groupingWeeksByStudyId(participateWeeks3, studyC.getId())).containsExactlyInAnyOrder(1),
                () -> assertThat(groupingWeeksByStudyId(participateWeeks2, studyD.getId())).containsExactlyInAnyOrder(1, 2),
                () -> assertThat(groupingWeeksByStudyId(participateWeeks3, studyE.getId())).containsExactlyInAnyOrder(1, 2)
        );
    }

    private List<Integer> groupingWeeksByStudyId(
            final List<StudyParticipateWeeks> participateWeeks,
            final Long studyId
    ) {
        return participateWeeks.stream()
                .filter(participateWeek -> participateWeek.studyId().equals(studyId))
                .map(StudyParticipateWeeks::week)
                .toList();
    }
}
