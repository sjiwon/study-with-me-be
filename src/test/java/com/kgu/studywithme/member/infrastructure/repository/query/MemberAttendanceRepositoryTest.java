package com.kgu.studywithme.member.infrastructure.repository.query;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.member.infrastructure.repository.query.dto.StudyParticipateWeeks;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.studyattendance.domain.StudyAttendance;
import com.kgu.studywithme.studyattendance.domain.StudyAttendanceRepository;
import com.kgu.studywithme.studyparticipant.domain.StudyParticipant;
import com.kgu.studywithme.studyparticipant.domain.StudyParticipantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.kgu.studywithme.fixture.MemberFixture.GHOST;
import static com.kgu.studywithme.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.fixture.StudyFixture.*;
import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.*;
import static com.kgu.studywithme.studyparticipant.domain.ParticipantStatus.APPROVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member -> MemberAttendanceRepository 테스트")
class MemberAttendanceRepositoryTest extends RepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private StudyParticipantRepository studyParticipantRepository;

    @Autowired
    private StudyAttendanceRepository studyAttendanceRepository;

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
    @DisplayName("사용자가 출석을 진행한 모든 스터디의 주차를 조회한다")
    void findParticipateWeeksInStudyByMemberId() {
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

        final List<StudyParticipateWeeks> participateWeeks1 = memberRepository.findParticipateWeeksInStudyByMemberId(member.getId());
        assertAll(
                () -> assertThat(groupingWeeksByStudyId(participateWeeks1, studyA.getId())).containsExactlyInAnyOrder(1),
                () -> assertThat(groupingWeeksByStudyId(participateWeeks1, studyB.getId())).containsExactlyInAnyOrder(1),
                () -> assertThat(groupingWeeksByStudyId(participateWeeks1, studyC.getId())).containsExactlyInAnyOrder(1),
                () -> assertThat(groupingWeeksByStudyId(participateWeeks1, studyD.getId())).containsExactlyInAnyOrder(1),
                () -> assertThat(groupingWeeksByStudyId(participateWeeks1, studyE.getId())).containsExactlyInAnyOrder(1)
        );

        /* Week 2 */
        studyAttendanceRepository.saveAll(
                List.of(
                        StudyAttendance.recordAttendance(studyA.getId(), member.getId(), 2, ATTENDANCE),
                        StudyAttendance.recordAttendance(studyD.getId(), member.getId(), 2, ATTENDANCE),
                        StudyAttendance.recordAttendance(studyE.getId(), member.getId(), 2, ABSENCE)
                )
        );

        final List<StudyParticipateWeeks> participateWeeks2 = memberRepository.findParticipateWeeksInStudyByMemberId(member.getId());
        assertAll(
                () -> assertThat(groupingWeeksByStudyId(participateWeeks2, studyA.getId())).containsExactlyInAnyOrder(1, 2),
                () -> assertThat(groupingWeeksByStudyId(participateWeeks2, studyB.getId())).containsExactlyInAnyOrder(1),
                () -> assertThat(groupingWeeksByStudyId(participateWeeks2, studyC.getId())).containsExactlyInAnyOrder(1),
                () -> assertThat(groupingWeeksByStudyId(participateWeeks2, studyD.getId())).containsExactlyInAnyOrder(1, 2),
                () -> assertThat(groupingWeeksByStudyId(participateWeeks2, studyE.getId())).containsExactlyInAnyOrder(1, 2)
        );

        /* Week 3 */
        studyAttendanceRepository.save(StudyAttendance.recordAttendance(studyA.getId(), member.getId(), 3, ATTENDANCE));

        final List<StudyParticipateWeeks> participateWeeks3 = memberRepository.findParticipateWeeksInStudyByMemberId(member.getId());
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
