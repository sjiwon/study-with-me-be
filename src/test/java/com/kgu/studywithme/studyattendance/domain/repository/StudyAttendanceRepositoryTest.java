package com.kgu.studywithme.studyattendance.domain.repository;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.studyattendance.domain.model.StudyAttendance;
import com.kgu.studywithme.studyweekly.domain.repository.StudyWeeklyRepository;
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
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY5;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_1;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_2;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_3;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.ABSENCE;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.ATTENDANCE;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.LATE;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.NON_ATTENDANCE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("StudyAttendance -> StudyAttendanceRepository 테스트")
public class StudyAttendanceRepositoryTest extends RepositoryTest {
    @Autowired
    private StudyAttendanceRepository sut;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private StudyWeeklyRepository studyWeeklyRepository;

    private final Member[] members = new Member[5];
    private Study study;

    @BeforeEach
    void setUp() {
        members[0] = memberRepository.save(DUMMY1.toMember());
        members[1] = memberRepository.save(DUMMY2.toMember());
        members[2] = memberRepository.save(DUMMY3.toMember());
        members[3] = memberRepository.save(DUMMY4.toMember());
        members[4] = memberRepository.save(DUMMY5.toMember());
        study = studyRepository.save(SPRING.toOnlineStudy(members[0].getId()));
    }

    @Test
    @DisplayName("특정 스터디에서 사용자의 특정 주차 출석 정보를 조회한다")
    void getParticipantAttendanceByWeek() {
        // given
        final StudyAttendance attendance
                = sut.save(StudyAttendance.recordAttendance(study.getId(), members[0].getId(), 1, ATTENDANCE));

        // when
        final Optional<StudyAttendance> findStudyAttendance
                = sut.findParticipantAttendanceByWeek(study.getId(), members[0].getId(), attendance.getWeek());
        final Optional<StudyAttendance> emptyStudyAttendance
                = sut.findParticipantAttendanceByWeek(study.getId(), members[0].getId(), attendance.getWeek() + 1);

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
        sut.saveAll(List.of(
                StudyAttendance.recordAttendance(study.getId(), members[0].getId(), 1, NON_ATTENDANCE),
                StudyAttendance.recordAttendance(study.getId(), members[1].getId(), 1, NON_ATTENDANCE),
                StudyAttendance.recordAttendance(study.getId(), members[2].getId(), 1, NON_ATTENDANCE),
                StudyAttendance.recordAttendance(study.getId(), members[3].getId(), 1, NON_ATTENDANCE),
                StudyAttendance.recordAttendance(study.getId(), members[4].getId(), 1, NON_ATTENDANCE)
        ));

        // when
        final Set<Long> participantIds = Set.of(members[1].getId(), members[3].getId());
        sut.updateParticipantStatus(study.getId(), 1, participantIds, ATTENDANCE);

        // then
        final List<StudyAttendance> attendances = sut.findAll();

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

    @Test
    @DisplayName("AttendanceStatus별 StudyAttendance 정보를 조회한다")
    void findByStatusOrderByStudyIdAscWeekAscParticipantIdAsc() {
        /* 1주차 */
        final StudyAttendance week1FromMember0 = sut.save(StudyAttendance.recordAttendance(study.getId(), members[0].getId(), 1, LATE));
        final StudyAttendance week1FromMember1 = sut.save(StudyAttendance.recordAttendance(study.getId(), members[1].getId(), 1, LATE));
        final StudyAttendance week1FromMember2 = sut.save(StudyAttendance.recordAttendance(study.getId(), members[2].getId(), 1, ATTENDANCE));
        final StudyAttendance week1FromMember3 = sut.save(StudyAttendance.recordAttendance(study.getId(), members[3].getId(), 1, ATTENDANCE));
        final StudyAttendance week1FromMember4 = sut.save(StudyAttendance.recordAttendance(study.getId(), members[4].getId(), 1, ABSENCE));

        final List<StudyAttendance> attendance1 = sut.findByStatusOrderByStudyIdAscWeekAscParticipantIdAsc(ATTENDANCE);
        final List<StudyAttendance> late1 = sut.findByStatusOrderByStudyIdAscWeekAscParticipantIdAsc(LATE);
        final List<StudyAttendance> absence1 = sut.findByStatusOrderByStudyIdAscWeekAscParticipantIdAsc(ABSENCE);
        final List<StudyAttendance> nonAttendance1 = sut.findByStatusOrderByStudyIdAscWeekAscParticipantIdAsc(NON_ATTENDANCE);
        assertAll(
                () -> assertThat(attendance1).hasSize(2),
                () -> assertThat(attendance1).containsExactly(week1FromMember2, week1FromMember3),
                () -> assertThat(late1).hasSize(2),
                () -> assertThat(late1).containsExactly(week1FromMember0, week1FromMember1),
                () -> assertThat(absence1).hasSize(1),
                () -> assertThat(absence1).containsExactly(week1FromMember4),
                () -> assertThat(nonAttendance1).hasSize(0),
                () -> assertThat(nonAttendance1).isEmpty()
        );

        /* 2주차 */
        final StudyAttendance week2FromMember0 = sut.save(StudyAttendance.recordAttendance(study.getId(), members[0].getId(), 2, ATTENDANCE));
        final StudyAttendance week2FromMember1 = sut.save(StudyAttendance.recordAttendance(study.getId(), members[1].getId(), 2, NON_ATTENDANCE));
        final StudyAttendance week2FromMember2 = sut.save(StudyAttendance.recordAttendance(study.getId(), members[2].getId(), 2, ATTENDANCE));
        final StudyAttendance week2FromMember3 = sut.save(StudyAttendance.recordAttendance(study.getId(), members[3].getId(), 2, NON_ATTENDANCE));
        final StudyAttendance week2FromMember4 = sut.save(StudyAttendance.recordAttendance(study.getId(), members[4].getId(), 2, ATTENDANCE));

        final List<StudyAttendance> attendance2 = sut.findByStatusOrderByStudyIdAscWeekAscParticipantIdAsc(ATTENDANCE);
        final List<StudyAttendance> late2 = sut.findByStatusOrderByStudyIdAscWeekAscParticipantIdAsc(LATE);
        final List<StudyAttendance> absence2 = sut.findByStatusOrderByStudyIdAscWeekAscParticipantIdAsc(ABSENCE);
        final List<StudyAttendance> nonAttendance2 = sut.findByStatusOrderByStudyIdAscWeekAscParticipantIdAsc(NON_ATTENDANCE);
        assertAll(
                () -> assertThat(attendance2).hasSize(5),
                () -> assertThat(attendance2).containsExactly(week1FromMember2, week1FromMember3, week2FromMember0, week2FromMember2, week2FromMember4),
                () -> assertThat(late2).hasSize(2),
                () -> assertThat(late2).containsExactly(week1FromMember0, week1FromMember1),
                () -> assertThat(absence2).hasSize(1),
                () -> assertThat(absence2).containsExactly(week1FromMember4),
                () -> assertThat(nonAttendance2).hasSize(2),
                () -> assertThat(nonAttendance2).containsExactly(week2FromMember1, week2FromMember3)
        );
    }

    @Test
    @DisplayName("사용자의 AttendanceStatus별 Count를 조회한다")
    void countByStudyIdAndParticipantIdAndStatus() {
        /* 출석 1회 */
        sut.save(StudyAttendance.recordAttendance(study.getId(), members[0].getId(), 1, ATTENDANCE));
        assertAll(
                () -> assertThat(sut.getAttendanceStatusCount(study.getId(), members[0].getId())).isEqualTo(1),
                () -> assertThat(sut.getLateStatusCount(study.getId(), members[0].getId())).isEqualTo(0),
                () -> assertThat(sut.getAbsenceStatusCount(study.getId(), members[0].getId())).isEqualTo(0),
                () -> assertThat(sut.getNonAttendanceStatusCount(study.getId(), members[0].getId())).isEqualTo(0)
        );

        /* 출석 1회 + 지각 1회 */
        sut.save(StudyAttendance.recordAttendance(study.getId(), members[0].getId(), 2, LATE));
        assertAll(
                () -> assertThat(sut.getAttendanceStatusCount(study.getId(), members[0].getId())).isEqualTo(1),
                () -> assertThat(sut.getLateStatusCount(study.getId(), members[0].getId())).isEqualTo(1),
                () -> assertThat(sut.getAbsenceStatusCount(study.getId(), members[0].getId())).isEqualTo(0),
                () -> assertThat(sut.getNonAttendanceStatusCount(study.getId(), members[0].getId())).isEqualTo(0)
        );

        /* 출석 2회 + 지각 1회 */
        sut.save(StudyAttendance.recordAttendance(study.getId(), members[0].getId(), 3, ATTENDANCE));
        assertAll(
                () -> assertThat(sut.getAttendanceStatusCount(study.getId(), members[0].getId())).isEqualTo(2),
                () -> assertThat(sut.getLateStatusCount(study.getId(), members[0].getId())).isEqualTo(1),
                () -> assertThat(sut.getAbsenceStatusCount(study.getId(), members[0].getId())).isEqualTo(0),
                () -> assertThat(sut.getNonAttendanceStatusCount(study.getId(), members[0].getId())).isEqualTo(0)
        );

        /* 출석 2회 + 지각 1회 + 미출석 1회 */
        sut.save(StudyAttendance.recordAttendance(study.getId(), members[0].getId(), 4, NON_ATTENDANCE));
        assertAll(
                () -> assertThat(sut.getAttendanceStatusCount(study.getId(), members[0].getId())).isEqualTo(2),
                () -> assertThat(sut.getLateStatusCount(study.getId(), members[0].getId())).isEqualTo(1),
                () -> assertThat(sut.getAbsenceStatusCount(study.getId(), members[0].getId())).isEqualTo(0),
                () -> assertThat(sut.getNonAttendanceStatusCount(study.getId(), members[0].getId())).isEqualTo(1)
        );
    }

    @Test
    @DisplayName("특정 Weekly의 출석 정보를 삭제한다")
    void deleteFromSpecificWeekly() {
        /* 3 Weekly & Attendance Information */
        studyWeeklyRepository.saveAll(List.of(
                STUDY_WEEKLY_1.toWeekly(study.getId(), members[0].getId()),
                STUDY_WEEKLY_2.toWeekly(study.getId(), members[0].getId()),
                STUDY_WEEKLY_3.toWeekly(study.getId(), members[0].getId())
        ));

        sut.saveAll(List.of(
                // Weekly 1
                StudyAttendance.recordAttendance(study.getId(), members[0].getId(), STUDY_WEEKLY_1.getWeek(), ATTENDANCE),
                StudyAttendance.recordAttendance(study.getId(), members[1].getId(), STUDY_WEEKLY_1.getWeek(), ATTENDANCE),
                StudyAttendance.recordAttendance(study.getId(), members[2].getId(), STUDY_WEEKLY_1.getWeek(), ATTENDANCE),
                StudyAttendance.recordAttendance(study.getId(), members[3].getId(), STUDY_WEEKLY_1.getWeek(), ATTENDANCE),

                // Weekly 2
                StudyAttendance.recordAttendance(study.getId(), members[0].getId(), STUDY_WEEKLY_2.getWeek(), ATTENDANCE),
                StudyAttendance.recordAttendance(study.getId(), members[1].getId(), STUDY_WEEKLY_2.getWeek(), ATTENDANCE),
                StudyAttendance.recordAttendance(study.getId(), members[2].getId(), STUDY_WEEKLY_2.getWeek(), ATTENDANCE),
                StudyAttendance.recordAttendance(study.getId(), members[3].getId(), STUDY_WEEKLY_2.getWeek(), ATTENDANCE),
                StudyAttendance.recordAttendance(study.getId(), members[4].getId(), STUDY_WEEKLY_2.getWeek(), ATTENDANCE),

                // Weekly 3
                StudyAttendance.recordAttendance(study.getId(), members[0].getId(), STUDY_WEEKLY_3.getWeek(), ATTENDANCE),
                StudyAttendance.recordAttendance(study.getId(), members[1].getId(), STUDY_WEEKLY_3.getWeek(), ATTENDANCE),
                StudyAttendance.recordAttendance(study.getId(), members[2].getId(), STUDY_WEEKLY_3.getWeek(), ATTENDANCE)
        ));

        assertAll(
                () -> assertThat(sut.existsByStudyIdAndWeek(study.getId(), STUDY_WEEKLY_1.getWeek())).isTrue(),
                () -> assertThat(sut.countByStudyIdAndWeek(study.getId(), STUDY_WEEKLY_1.getWeek())).isEqualTo(4),
                () -> assertThat(sut.existsByStudyIdAndWeek(study.getId(), STUDY_WEEKLY_2.getWeek())).isTrue(),
                () -> assertThat(sut.countByStudyIdAndWeek(study.getId(), STUDY_WEEKLY_2.getWeek())).isEqualTo(5),
                () -> assertThat(sut.existsByStudyIdAndWeek(study.getId(), STUDY_WEEKLY_3.getWeek())).isTrue(),
                () -> assertThat(sut.countByStudyIdAndWeek(study.getId(), STUDY_WEEKLY_3.getWeek())).isEqualTo(3)
        );

        /* 3주차 출석 정보 삭제 */
        final int deleteWeekly3 = sut.deleteFromSpecificWeekly(study.getId(), STUDY_WEEKLY_3.getWeek());
        assertAll(
                () -> assertThat(deleteWeekly3).isEqualTo(3),
                () -> assertThat(sut.existsByStudyIdAndWeek(study.getId(), STUDY_WEEKLY_1.getWeek())).isTrue(),
                () -> assertThat(sut.countByStudyIdAndWeek(study.getId(), STUDY_WEEKLY_1.getWeek())).isEqualTo(4),
                () -> assertThat(sut.existsByStudyIdAndWeek(study.getId(), STUDY_WEEKLY_2.getWeek())).isTrue(),
                () -> assertThat(sut.countByStudyIdAndWeek(study.getId(), STUDY_WEEKLY_2.getWeek())).isEqualTo(5),
                () -> assertThat(sut.existsByStudyIdAndWeek(study.getId(), STUDY_WEEKLY_3.getWeek())).isFalse()
        );

        /* 2주차 출석 정보 삭제 */
        final int deleteWeekly2 = sut.deleteFromSpecificWeekly(study.getId(), STUDY_WEEKLY_2.getWeek());
        assertAll(
                () -> assertThat(deleteWeekly2).isEqualTo(5),
                () -> assertThat(sut.existsByStudyIdAndWeek(study.getId(), STUDY_WEEKLY_1.getWeek())).isTrue(),
                () -> assertThat(sut.countByStudyIdAndWeek(study.getId(), STUDY_WEEKLY_1.getWeek())).isEqualTo(4),
                () -> assertThat(sut.existsByStudyIdAndWeek(study.getId(), STUDY_WEEKLY_2.getWeek())).isFalse(),
                () -> assertThat(sut.existsByStudyIdAndWeek(study.getId(), STUDY_WEEKLY_3.getWeek())).isFalse()
        );

        /* 1주차 출석 정보 삭제 */
        final int deleteWeekly1 = sut.deleteFromSpecificWeekly(study.getId(), STUDY_WEEKLY_1.getWeek());
        assertAll(
                () -> assertThat(deleteWeekly1).isEqualTo(4),
                () -> assertThat(sut.existsByStudyIdAndWeek(study.getId(), STUDY_WEEKLY_1.getWeek())).isFalse(),
                () -> assertThat(sut.existsByStudyIdAndWeek(study.getId(), STUDY_WEEKLY_2.getWeek())).isFalse(),
                () -> assertThat(sut.existsByStudyIdAndWeek(study.getId(), STUDY_WEEKLY_3.getWeek())).isFalse()
        );
    }
}
