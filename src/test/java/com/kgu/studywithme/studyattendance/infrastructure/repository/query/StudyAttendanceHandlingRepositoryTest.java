package com.kgu.studywithme.studyattendance.infrastructure.repository.query;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
import com.kgu.studywithme.studyattendance.domain.StudyAttendance;
import com.kgu.studywithme.studyattendance.domain.StudyAttendanceRepository;
import com.kgu.studywithme.studyattendance.infrastructure.repository.query.dto.NonAttendanceWeekly;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.kgu.studywithme.common.fixture.MemberFixture.*;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("StudyAttendance -> StudyAttendanceHandlingRepository 테스트")
class StudyAttendanceHandlingRepositoryTest extends RepositoryTest {
    @Autowired
    private StudyAttendanceRepository studyAttendanceRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StudyRepository studyRepository;

    private final Member[] member = new Member[5];
    private Study study;

    @BeforeEach
    void setUp() {
        member[0] = memberRepository.save(JIWON.toMember());
        member[1] = memberRepository.save(DUMMY1.toMember());
        member[2] = memberRepository.save(DUMMY2.toMember());
        member[3] = memberRepository.save(DUMMY3.toMember());
        member[4] = memberRepository.save(DUMMY4.toMember());
        study = studyRepository.save(SPRING.toOnlineStudy(member[0].getId()));
    }

    @Test
    @DisplayName("특정 스터디에서 사용자의 특정 주차 출석 정보를 조회한다")
    void getParticipantAttendanceByWeek() {
        // given
        final StudyAttendance attendance = studyAttendanceRepository.save(
                StudyAttendance.recordAttendance(
                        study.getId(),
                        member[0].getId(),
                        1,
                        ATTENDANCE
                )
        );

        // when
        final Optional<StudyAttendance> findStudyAttendance = studyAttendanceRepository.getParticipantAttendanceByWeek(
                study.getId(),
                member[0].getId(),
                attendance.getWeek()
        );
        final Optional<StudyAttendance> emptyStudyAttendance = studyAttendanceRepository.getParticipantAttendanceByWeek(
                study.getId(),
                member[0].getId(),
                attendance.getWeek() + 1
        );

        // then
        assertAll(
                () -> assertThat(findStudyAttendance).isPresent(),
                () -> assertThat(emptyStudyAttendance).isEmpty()
        );
    }

    @Test
    @DisplayName("스터디 참여자들 중에서 일부 참여자들의 특정 주차 출석 상태를 일괄 업데이트한다")
    void updateParticipantStatus() {
        // given
        studyAttendanceRepository.saveAll(
                List.of(
                        StudyAttendance.recordAttendance(study.getId(), member[0].getId(), 1, NON_ATTENDANCE),
                        StudyAttendance.recordAttendance(study.getId(), member[1].getId(), 1, NON_ATTENDANCE),
                        StudyAttendance.recordAttendance(study.getId(), member[2].getId(), 1, NON_ATTENDANCE),
                        StudyAttendance.recordAttendance(study.getId(), member[3].getId(), 1, NON_ATTENDANCE),
                        StudyAttendance.recordAttendance(study.getId(), member[4].getId(), 1, NON_ATTENDANCE)
                )
        );

        // when
        final Set<Long> participantIds = Set.of(member[1].getId(), member[3].getId());
        studyAttendanceRepository.updateParticipantStatus(
                study.getId(),
                1,
                participantIds,
                ATTENDANCE
        );

        // then
        final List<StudyAttendance> attendances = studyAttendanceRepository.findAll();

        assertAll(
                () -> assertThat(attendances)
                        .map(StudyAttendance::getParticipantId)
                        .containsExactly(
                                member[0].getId(),
                                member[1].getId(),
                                member[2].getId(),
                                member[3].getId(),
                                member[4].getId()
                        ),
                () -> assertThat(attendances)
                        .map(StudyAttendance::getStatus)
                        .containsExactly(
                                NON_ATTENDANCE,
                                ATTENDANCE,
                                NON_ATTENDANCE,
                                ATTENDANCE,
                                NON_ATTENDANCE
                        )
        );
    }

    @Test
    @DisplayName("사용자의 스터디 출석 횟수를 조회한다")
    void getAttendanceCount() {
        /* 출석 1회 */
        studyAttendanceRepository.save(
                StudyAttendance.recordAttendance(study.getId(), member[0].getId(), 1, ATTENDANCE)
        );
        assertThat(studyAttendanceRepository.getAttendanceCount(study.getId(), member[0].getId())).isEqualTo(1);

        /* 출석 1회 + 지각 1회 */
        studyAttendanceRepository.save(
                StudyAttendance.recordAttendance(study.getId(), member[0].getId(), 2, LATE)
        );
        assertThat(studyAttendanceRepository.getAttendanceCount(study.getId(), member[0].getId())).isEqualTo(1);

        /* 출석 2회 + 지각 1회 */
        studyAttendanceRepository.save(
                StudyAttendance.recordAttendance(study.getId(), member[0].getId(), 3, ATTENDANCE)
        );
        assertThat(studyAttendanceRepository.getAttendanceCount(study.getId(), member[0].getId())).isEqualTo(2);
    }

    @Test
    @DisplayName("미출석 주차 정보들을 조회한다")
    void findNonAttendanceInformation() {
        /* 1주차 */
        studyAttendanceRepository.saveAll(
                List.of(
                        StudyAttendance.recordAttendance(study.getId(), member[0].getId(), 1, NON_ATTENDANCE),
                        StudyAttendance.recordAttendance(study.getId(), member[1].getId(), 1, NON_ATTENDANCE),
                        StudyAttendance.recordAttendance(study.getId(), member[2].getId(), 1, ATTENDANCE),
                        StudyAttendance.recordAttendance(study.getId(), member[3].getId(), 1, ATTENDANCE),
                        StudyAttendance.recordAttendance(study.getId(), member[4].getId(), 1, NON_ATTENDANCE)
                )
        );

        final List<NonAttendanceWeekly> result1 = studyAttendanceRepository.findNonAttendanceInformation();
        assertAll(
                () -> assertThat(result1)
                        .map(NonAttendanceWeekly::studyId)
                        .containsExactly(study.getId(), study.getId(), study.getId()),
                () -> assertThat(result1)
                        .map(NonAttendanceWeekly::week)
                        .containsExactly(1, 1, 1),
                () -> assertThat(result1)
                        .map(NonAttendanceWeekly::participantId)
                        .containsExactly(member[0].getId(), member[1].getId(), member[4].getId())
        );

        /* 2주차 */
        studyAttendanceRepository.saveAll(
                List.of(
                        StudyAttendance.recordAttendance(study.getId(), member[0].getId(), 2, ATTENDANCE),
                        StudyAttendance.recordAttendance(study.getId(), member[1].getId(), 2, NON_ATTENDANCE),
                        StudyAttendance.recordAttendance(study.getId(), member[2].getId(), 2, ATTENDANCE),
                        StudyAttendance.recordAttendance(study.getId(), member[3].getId(), 2, NON_ATTENDANCE),
                        StudyAttendance.recordAttendance(study.getId(), member[4].getId(), 2, ATTENDANCE)
                )
        );

        final List<NonAttendanceWeekly> result2 = studyAttendanceRepository.findNonAttendanceInformation();
        assertAll(
                () -> assertThat(result2)
                        .map(NonAttendanceWeekly::studyId)
                        .containsExactly(
                                study.getId(), study.getId(), study.getId(),
                                study.getId(), study.getId()
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
                                member[0].getId(), member[1].getId(), member[4].getId(),
                                member[1].getId(), member[3].getId()
                        )
        );
    }
}
