package com.kgu.studywithme.studyattendance.domain.repository.query;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.studyattendance.domain.model.StudyAttendance;
import com.kgu.studywithme.studyattendance.domain.repository.StudyAttendanceRepository;
import com.kgu.studywithme.studyattendance.domain.repository.query.dto.StudyAttendanceWeekly;
import com.kgu.studywithme.studyparticipant.domain.model.StudyParticipant;
import com.kgu.studywithme.studyparticipant.domain.repository.StudyParticipantRepository;
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
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.ABSENCE;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.ATTENDANCE;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.LATE;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.APPROVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(StudyAttendanceMetadataRepositoryImpl.class)
@DisplayName("StudyAttendance -> StudyAttendanceMetadataRepository 테스트")
class StudyAttendanceMetadataRepositoryTest extends RepositoryTest {
    @Autowired
    private StudyAttendanceMetadataRepositoryImpl sut;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private StudyAttendanceRepository studyAttendanceRepository;

    @Autowired
    private StudyParticipantRepository studyParticipantRepository;

    private Member participant;
    private final Study[] studies = new Study[5];

    @BeforeEach
    void setUp() {
        participant = memberRepository.save(JIWON.toMember());

        final Member host = memberRepository.save(GHOST.toMember());
        studies[0] = studyRepository.save(SPRING.toStudy(host));
        studies[1] = studyRepository.save(JPA.toStudy(host));
        studies[2] = studyRepository.save(KOTLIN.toStudy(host));
        studies[3] = studyRepository.save(NETWORK.toStudy(host));
        studies[4] = studyRepository.save(EFFECTIVE_JAVA.toStudy(host));
    }

    @Test
    @DisplayName("사용자가 출석을 진행한 모든 스터디의 주차를 조회한다")
    void findParticipateWeeksInStudyByMemberId() {
        /* 모든 스터디 참여 */
        studyParticipantRepository.saveAll(List.of(
                StudyParticipant.applyParticipant(studies[0].getId(), participant.getId(), APPROVE),
                StudyParticipant.applyParticipant(studies[1].getId(), participant.getId(), APPROVE),
                StudyParticipant.applyParticipant(studies[2].getId(), participant.getId(), APPROVE),
                StudyParticipant.applyParticipant(studies[3].getId(), participant.getId(), APPROVE),
                StudyParticipant.applyParticipant(studies[4].getId(), participant.getId(), APPROVE)
        ));

        /* Week 1 */
        studyAttendanceRepository.saveAll(List.of(
                StudyAttendance.recordAttendance(studies[0].getId(), participant.getId(), 1, ATTENDANCE),
                StudyAttendance.recordAttendance(studies[1].getId(), participant.getId(), 1, ATTENDANCE),
                StudyAttendance.recordAttendance(studies[2].getId(), participant.getId(), 1, LATE),
                StudyAttendance.recordAttendance(studies[3].getId(), participant.getId(), 1, ATTENDANCE),
                StudyAttendance.recordAttendance(studies[4].getId(), participant.getId(), 1, ABSENCE)
        ));

        final List<StudyAttendanceWeekly> participateWeeks1 = sut.findMemberParticipateWeekly(participant.getId());
        assertAll(
                () -> assertThat(groupingWeeksByStudyId(participateWeeks1, studies[0].getId())).containsExactlyInAnyOrder(1),
                () -> assertThat(groupingWeeksByStudyId(participateWeeks1, studies[1].getId())).containsExactlyInAnyOrder(1),
                () -> assertThat(groupingWeeksByStudyId(participateWeeks1, studies[2].getId())).containsExactlyInAnyOrder(1),
                () -> assertThat(groupingWeeksByStudyId(participateWeeks1, studies[3].getId())).containsExactlyInAnyOrder(1),
                () -> assertThat(groupingWeeksByStudyId(participateWeeks1, studies[4].getId())).containsExactlyInAnyOrder(1)
        );

        /* Week 2 */
        studyAttendanceRepository.saveAll(List.of(
                StudyAttendance.recordAttendance(studies[0].getId(), participant.getId(), 2, ATTENDANCE),
                StudyAttendance.recordAttendance(studies[3].getId(), participant.getId(), 2, ATTENDANCE),
                StudyAttendance.recordAttendance(studies[4].getId(), participant.getId(), 2, ABSENCE)
        ));

        final List<StudyAttendanceWeekly> participateWeeks2 = sut.findMemberParticipateWeekly(participant.getId());
        assertAll(
                () -> assertThat(groupingWeeksByStudyId(participateWeeks2, studies[0].getId())).containsExactlyInAnyOrder(1, 2),
                () -> assertThat(groupingWeeksByStudyId(participateWeeks2, studies[1].getId())).containsExactlyInAnyOrder(1),
                () -> assertThat(groupingWeeksByStudyId(participateWeeks2, studies[2].getId())).containsExactlyInAnyOrder(1),
                () -> assertThat(groupingWeeksByStudyId(participateWeeks2, studies[3].getId())).containsExactlyInAnyOrder(1, 2),
                () -> assertThat(groupingWeeksByStudyId(participateWeeks2, studies[4].getId())).containsExactlyInAnyOrder(1, 2)
        );

        /* Week 3 */
        studyAttendanceRepository.save(StudyAttendance.recordAttendance(studies[0].getId(), participant.getId(), 3, ATTENDANCE));

        final List<StudyAttendanceWeekly> participateWeeks3 = sut.findMemberParticipateWeekly(participant.getId());
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
}
