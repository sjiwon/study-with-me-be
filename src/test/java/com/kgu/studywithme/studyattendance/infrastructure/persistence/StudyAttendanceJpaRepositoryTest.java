package com.kgu.studywithme.studyattendance.infrastructure.persistence;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.infrastructure.persistence.MemberJpaRepository;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.infrastructure.persistence.StudyJpaRepository;
import com.kgu.studywithme.studyattendance.domain.StudyAttendance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY1;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY2;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY3;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY4;
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.ATTENDANCE;
import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.NON_ATTENDANCE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("StudyAttendance -> StudyAttendanceJpaRepository 테스트")
public class StudyAttendanceJpaRepositoryTest extends RepositoryTest {
    @Autowired
    private StudyAttendanceJpaRepository studyAttendanceJpaRepository;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private StudyJpaRepository studyJpaRepository;

    private final Member[] member = new Member[5];
    private Study study;

    @BeforeEach
    void setUp() {
        member[0] = memberJpaRepository.save(JIWON.toMember());
        member[1] = memberJpaRepository.save(DUMMY1.toMember());
        member[2] = memberJpaRepository.save(DUMMY2.toMember());
        member[3] = memberJpaRepository.save(DUMMY3.toMember());
        member[4] = memberJpaRepository.save(DUMMY4.toMember());
        study = studyJpaRepository.save(SPRING.toOnlineStudy(member[0].getId()));
    }

    @Test
    @DisplayName("특정 스터디에서 사용자의 특정 주차 출석 정보를 조회한다")
    void getParticipantAttendanceByWeek() {
        // given
        final StudyAttendance attendance = studyAttendanceJpaRepository.save(
                StudyAttendance.recordAttendance(
                        study.getId(),
                        member[0].getId(),
                        1,
                        ATTENDANCE
                )
        );

        // when
        final Optional<StudyAttendance> findStudyAttendance = studyAttendanceJpaRepository.getParticipantAttendanceByWeek(
                study.getId(),
                member[0].getId(),
                attendance.getWeek()
        );
        final Optional<StudyAttendance> emptyStudyAttendance = studyAttendanceJpaRepository.getParticipantAttendanceByWeek(
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
        studyAttendanceJpaRepository.saveAll(
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
        studyAttendanceJpaRepository.updateParticipantStatus(
                study.getId(),
                1,
                participantIds,
                ATTENDANCE
        );

        // then
        final List<StudyAttendance> attendances = studyAttendanceJpaRepository.findAll();

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
}
