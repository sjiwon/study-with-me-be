package com.kgu.studywithme.studyattendance.domain;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.domain.MemberRepository;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.domain.StudyRepository;
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

@DisplayName("StudyAttendance -> StudyAttendanceRepository 테스트")
public class StudyAttendanceRepositoryTest extends RepositoryTest {
    @Autowired
    private StudyAttendanceRepository studyAttendanceRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StudyRepository studyRepository;

    private final Member[] members = new Member[5];
    private Study study;

    @BeforeEach
    void setUp() {
        members[0] = memberRepository.save(JIWON.toMember());
        members[1] = memberRepository.save(DUMMY1.toMember());
        members[2] = memberRepository.save(DUMMY2.toMember());
        members[3] = memberRepository.save(DUMMY3.toMember());
        members[4] = memberRepository.save(DUMMY4.toMember());
        study = studyRepository.save(SPRING.toOnlineStudy(members[0].getId()));
    }

    @Test
    @DisplayName("특정 스터디에서 사용자의 특정 주차 출석 정보를 조회한다")
    void getParticipantAttendanceByWeek() {
        // given
        final StudyAttendance attendance = studyAttendanceRepository.save(
                StudyAttendance.recordAttendance(
                        study.getId(),
                        members[0].getId(),
                        1,
                        ATTENDANCE
                )
        );

        // when
        final Optional<StudyAttendance> findStudyAttendance = studyAttendanceRepository.getParticipantAttendanceByWeek(
                study.getId(),
                members[0].getId(),
                attendance.getWeek()
        );
        final Optional<StudyAttendance> emptyStudyAttendance = studyAttendanceRepository.getParticipantAttendanceByWeek(
                study.getId(),
                members[0].getId(),
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
                        StudyAttendance.recordAttendance(study.getId(), members[0].getId(), 1, NON_ATTENDANCE),
                        StudyAttendance.recordAttendance(study.getId(), members[1].getId(), 1, NON_ATTENDANCE),
                        StudyAttendance.recordAttendance(study.getId(), members[2].getId(), 1, NON_ATTENDANCE),
                        StudyAttendance.recordAttendance(study.getId(), members[3].getId(), 1, NON_ATTENDANCE),
                        StudyAttendance.recordAttendance(study.getId(), members[4].getId(), 1, NON_ATTENDANCE)
                )
        );

        // when
        final Set<Long> participantIds = Set.of(members[1].getId(), members[3].getId());
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
                                members[0].getId(),
                                members[1].getId(),
                                members[2].getId(),
                                members[3].getId(),
                                members[4].getId()
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
