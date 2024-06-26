package com.kgu.studywithme.studyattendance.domain.service;

import com.kgu.studywithme.common.IntegrateTest;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.member.domain.model.Score;
import com.kgu.studywithme.member.domain.repository.MemberRepository;
import com.kgu.studywithme.study.domain.model.Study;
import com.kgu.studywithme.study.domain.repository.StudyRepository;
import com.kgu.studywithme.studyattendance.domain.model.StudyAttendance;
import com.kgu.studywithme.studyattendance.domain.repository.StudyAttendanceRepository;
import com.kgu.studywithme.studyparticipant.domain.model.StudyParticipant;
import com.kgu.studywithme.studyparticipant.domain.repository.StudyParticipantRepository;
import com.kgu.studywithme.studyweekly.domain.model.Period;
import com.kgu.studywithme.studyweekly.domain.model.StudyWeekly;
import com.kgu.studywithme.studyweekly.domain.repository.StudyWeeklyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY1;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY2;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY3;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY4;
import static com.kgu.studywithme.common.fixture.MemberFixture.DUMMY5;
import static com.kgu.studywithme.common.fixture.StudyFixture.JPA;
import static com.kgu.studywithme.common.fixture.StudyFixture.KOTLIN;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_1;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_2;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_3;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_4;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_5;
import static com.kgu.studywithme.common.fixture.StudyWeeklyFixture.STUDY_WEEKLY_6;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.ATTENDANCE;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.LATE;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.NON_ATTENDANCE;
import static com.kgu.studywithme.studyparticipant.domain.model.ParticipantStatus.APPROVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("StudyAttendance -> 매일 자정에 실행되는 Weekly별 결석한 참여자 Absence Score 적용 스케줄링 로직")
public class UpdateWeeklyAttendanceBatchProcessorTest extends IntegrateTest {
    @Autowired
    private UpdateWeeklyAttendanceBatchProcessor sut;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private StudyParticipantRepository studyParticipantRepository;

    @Autowired
    private StudyWeeklyRepository studyWeeklyRepository;

    @Autowired
    private StudyAttendanceRepository studyAttendanceRepository;

    private final Member[] participants = new Member[5];
    private final Study[] studies = new Study[3];
    private int previousScore;

    @BeforeEach
    void setUp() {
        participants[0] = memberRepository.save(DUMMY1.toMember());
        participants[1] = memberRepository.save(DUMMY2.toMember());
        participants[2] = memberRepository.save(DUMMY3.toMember());
        participants[3] = memberRepository.save(DUMMY4.toMember());
        participants[4] = memberRepository.save(DUMMY5.toMember());

        studies[0] = studyRepository.save(SPRING.toStudy(participants[0]));
        studies[1] = studyRepository.save(JPA.toStudy(participants[0]));
        studies[2] = studyRepository.save(KOTLIN.toStudy(participants[0]));

        previousScore = Score.INIT_SCORE;

        studyParticipantRepository.saveAll(List.of(
                StudyParticipant.applyParticipant(studies[0], participants[0], APPROVE),
                StudyParticipant.applyParticipant(studies[0], participants[1], APPROVE),
                StudyParticipant.applyParticipant(studies[0], participants[2], APPROVE),
                StudyParticipant.applyParticipant(studies[0], participants[3], APPROVE),
                StudyParticipant.applyParticipant(studies[0], participants[4], APPROVE),

                StudyParticipant.applyParticipant(studies[1], participants[0], APPROVE),
                StudyParticipant.applyParticipant(studies[1], participants[1], APPROVE),
                StudyParticipant.applyParticipant(studies[1], participants[2], APPROVE),
                StudyParticipant.applyParticipant(studies[1], participants[3], APPROVE),
                StudyParticipant.applyParticipant(studies[1], participants[4], APPROVE),

                StudyParticipant.applyParticipant(studies[2], participants[0], APPROVE),
                StudyParticipant.applyParticipant(studies[2], participants[1], APPROVE),
                StudyParticipant.applyParticipant(studies[2], participants[2], APPROVE),
                StudyParticipant.applyParticipant(studies[2], participants[3], APPROVE),
                StudyParticipant.applyParticipant(studies[2], participants[4], APPROVE)
        ));

        studyWeeklyRepository.saveAll(List.of(
                StudyWeekly.createWeeklyWithAssignment(
                        studies[0],
                        participants[0],
                        STUDY_WEEKLY_1.getTitle(),
                        STUDY_WEEKLY_1.getContent(),
                        1,
                        new Period(LocalDateTime.now().minusDays(7), LocalDateTime.now().minusDays(4)),
                        false,
                        List.of()
                ),
                StudyWeekly.createWeeklyWithAssignment(
                        studies[0],
                        participants[0],
                        STUDY_WEEKLY_2.getTitle(),
                        STUDY_WEEKLY_2.getContent(),
                        2,
                        new Period(LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1)),
                        true,
                        List.of()
                ),

                StudyWeekly.createWeeklyWithAssignment(
                        studies[1],
                        participants[0],
                        STUDY_WEEKLY_3.getTitle(),
                        STUDY_WEEKLY_3.getContent(),
                        1,
                        new Period(LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1)),
                        true,
                        List.of()
                ),
                StudyWeekly.createWeeklyWithAssignment(
                        studies[1],
                        participants[0],
                        STUDY_WEEKLY_4.getTitle(),
                        STUDY_WEEKLY_4.getContent(),
                        2,
                        new Period(LocalDateTime.now().minusHours(10), LocalDateTime.now().plusDays(2)),
                        true,
                        List.of()
                ),

                StudyWeekly.createWeeklyWithAssignment(
                        studies[2],
                        participants[0],
                        STUDY_WEEKLY_5.getTitle(),
                        STUDY_WEEKLY_5.getContent(),
                        1,
                        new Period(LocalDateTime.now().minusDays(2), LocalDateTime.now().minusHours(5)),
                        true,
                        List.of()
                ),
                StudyWeekly.createWeeklyWithAssignment(
                        studies[2],
                        participants[0],
                        STUDY_WEEKLY_6.getTitle(),
                        STUDY_WEEKLY_6.getContent(),
                        2,
                        new Period(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(4)),
                        true,
                        List.of()
                )
        ));
    }

    /**
     * 스터디 3개 + 2 Weeklys<br>
     * -> studies[0] = Week2 처리 대상<br>
     * -> studies[1] = Week1 처리 대상<br>
     * -> studies[2] = Week1 처리 대상
     */
    @Test
    @DisplayName("[Now-2..Now] 기간안에 Auto Attendance Weekly에 대한 미출석(NON_ATTENDANCE) 정보를 결석 처리한다")
    void execute() {
        // given
        studyAttendanceRepository.saveAll(List.of(
                StudyAttendance.recordAttendance(studies[0], participants[0], 1, ATTENDANCE),
                StudyAttendance.recordAttendance(studies[0], participants[1], 1, NON_ATTENDANCE),
                StudyAttendance.recordAttendance(studies[0], participants[2], 1, ATTENDANCE),
                StudyAttendance.recordAttendance(studies[0], participants[3], 1, LATE),
                StudyAttendance.recordAttendance(studies[0], participants[4], 1, NON_ATTENDANCE),
                StudyAttendance.recordAttendance(studies[0], participants[0], 2, NON_ATTENDANCE), // candidate
                StudyAttendance.recordAttendance(studies[0], participants[1], 2, ATTENDANCE),
                StudyAttendance.recordAttendance(studies[0], participants[2], 2, NON_ATTENDANCE), // candidate
                StudyAttendance.recordAttendance(studies[0], participants[3], 2, ATTENDANCE),
                StudyAttendance.recordAttendance(studies[0], participants[4], 2, NON_ATTENDANCE), // candidate

                StudyAttendance.recordAttendance(studies[1], participants[0], 1, ATTENDANCE),
                StudyAttendance.recordAttendance(studies[1], participants[1], 1, NON_ATTENDANCE), // candidate
                StudyAttendance.recordAttendance(studies[1], participants[2], 1, NON_ATTENDANCE), // candidate
                StudyAttendance.recordAttendance(studies[1], participants[3], 1, ATTENDANCE),
                StudyAttendance.recordAttendance(studies[1], participants[4], 1, ATTENDANCE),
                StudyAttendance.recordAttendance(studies[1], participants[0], 2, NON_ATTENDANCE),
                StudyAttendance.recordAttendance(studies[1], participants[1], 2, ATTENDANCE),
                StudyAttendance.recordAttendance(studies[1], participants[2], 2, ATTENDANCE),
                StudyAttendance.recordAttendance(studies[1], participants[3], 2, NON_ATTENDANCE),
                StudyAttendance.recordAttendance(studies[1], participants[4], 2, ATTENDANCE),

                StudyAttendance.recordAttendance(studies[2], participants[0], 1, NON_ATTENDANCE), // candidate
                StudyAttendance.recordAttendance(studies[2], participants[1], 1, ATTENDANCE),
                StudyAttendance.recordAttendance(studies[2], participants[2], 1, LATE),
                StudyAttendance.recordAttendance(studies[2], participants[3], 1, LATE),
                StudyAttendance.recordAttendance(studies[2], participants[4], 1, NON_ATTENDANCE), // candidate
                StudyAttendance.recordAttendance(studies[2], participants[0], 2, ATTENDANCE),
                StudyAttendance.recordAttendance(studies[2], participants[1], 2, NON_ATTENDANCE),
                StudyAttendance.recordAttendance(studies[2], participants[2], 2, ATTENDANCE),
                StudyAttendance.recordAttendance(studies[2], participants[3], 2, NON_ATTENDANCE),
                StudyAttendance.recordAttendance(studies[2], participants[4], 2, NON_ATTENDANCE)
        ));

        // when
        sut.checkAbsenceParticipantAndApplyAbsenceScore();

        // then
        /**
         * studies[0] - Week2
         * studies[2] - Week1
         */
        final int scoreOfParticipant0 = memberRepository.getById(participants[0].getId()).getScore().getValue();
        assertAll(
                () -> assertThat(studyAttendanceRepository.getAttendanceStatusCount(studies[0].getId(), participants[0].getId())).isEqualTo(1),
                () -> assertThat(studyAttendanceRepository.getLateStatusCount(studies[0].getId(), participants[0].getId())).isEqualTo(0),
                () -> assertThat(studyAttendanceRepository.getAbsenceStatusCount(studies[0].getId(), participants[0].getId())).isEqualTo(1),
                () -> assertThat(studyAttendanceRepository.getNonAttendanceStatusCount(studies[0].getId(), participants[0].getId())).isEqualTo(0),

                () -> assertThat(studyAttendanceRepository.getAttendanceStatusCount(studies[1].getId(), participants[0].getId())).isEqualTo(1),
                () -> assertThat(studyAttendanceRepository.getLateStatusCount(studies[1].getId(), participants[0].getId())).isEqualTo(0),
                () -> assertThat(studyAttendanceRepository.getAbsenceStatusCount(studies[1].getId(), participants[0].getId())).isEqualTo(0),
                () -> assertThat(studyAttendanceRepository.getNonAttendanceStatusCount(studies[1].getId(), participants[0].getId())).isEqualTo(1),

                () -> assertThat(studyAttendanceRepository.getAttendanceStatusCount(studies[2].getId(), participants[0].getId())).isEqualTo(1),
                () -> assertThat(studyAttendanceRepository.getLateStatusCount(studies[2].getId(), participants[0].getId())).isEqualTo(0),
                () -> assertThat(studyAttendanceRepository.getAbsenceStatusCount(studies[2].getId(), participants[0].getId())).isEqualTo(1),
                () -> assertThat(studyAttendanceRepository.getNonAttendanceStatusCount(studies[2].getId(), participants[0].getId())).isEqualTo(0),

                () -> assertThat(scoreOfParticipant0).isEqualTo(previousScore + Score.ABSENCE * 2)
        );

        /**
         * studies[1] - Week1
         */
        final int scoreOfParticipant1 = memberRepository.getById(participants[1].getId()).getScore().getValue();
        assertAll(
                () -> assertThat(studyAttendanceRepository.getAttendanceStatusCount(studies[0].getId(), participants[1].getId())).isEqualTo(1),
                () -> assertThat(studyAttendanceRepository.getLateStatusCount(studies[0].getId(), participants[1].getId())).isEqualTo(0),
                () -> assertThat(studyAttendanceRepository.getAbsenceStatusCount(studies[0].getId(), participants[1].getId())).isEqualTo(0),
                () -> assertThat(studyAttendanceRepository.getNonAttendanceStatusCount(studies[0].getId(), participants[1].getId())).isEqualTo(1),

                () -> assertThat(studyAttendanceRepository.getAttendanceStatusCount(studies[1].getId(), participants[1].getId())).isEqualTo(1),
                () -> assertThat(studyAttendanceRepository.getLateStatusCount(studies[1].getId(), participants[1].getId())).isEqualTo(0),
                () -> assertThat(studyAttendanceRepository.getAbsenceStatusCount(studies[1].getId(), participants[1].getId())).isEqualTo(1),
                () -> assertThat(studyAttendanceRepository.getNonAttendanceStatusCount(studies[1].getId(), participants[1].getId())).isEqualTo(0),

                () -> assertThat(studyAttendanceRepository.getAttendanceStatusCount(studies[2].getId(), participants[1].getId())).isEqualTo(1),
                () -> assertThat(studyAttendanceRepository.getLateStatusCount(studies[2].getId(), participants[1].getId())).isEqualTo(0),
                () -> assertThat(studyAttendanceRepository.getAbsenceStatusCount(studies[2].getId(), participants[1].getId())).isEqualTo(0),
                () -> assertThat(studyAttendanceRepository.getNonAttendanceStatusCount(studies[2].getId(), participants[1].getId())).isEqualTo(1),

                () -> assertThat(scoreOfParticipant1).isEqualTo(previousScore + Score.ABSENCE)
        );

        /**
         * studies[0] - Week2
         * studies[1] - Week1
         */
        final int scoreOfParticipant2 = memberRepository.getById(participants[2].getId()).getScore().getValue();
        assertAll(
                () -> assertThat(studyAttendanceRepository.getAttendanceStatusCount(studies[0].getId(), participants[2].getId())).isEqualTo(1),
                () -> assertThat(studyAttendanceRepository.getLateStatusCount(studies[0].getId(), participants[2].getId())).isEqualTo(0),
                () -> assertThat(studyAttendanceRepository.getAbsenceStatusCount(studies[0].getId(), participants[2].getId())).isEqualTo(1),
                () -> assertThat(studyAttendanceRepository.getNonAttendanceStatusCount(studies[0].getId(), participants[2].getId())).isEqualTo(0),

                () -> assertThat(studyAttendanceRepository.getAttendanceStatusCount(studies[1].getId(), participants[2].getId())).isEqualTo(1),
                () -> assertThat(studyAttendanceRepository.getLateStatusCount(studies[1].getId(), participants[2].getId())).isEqualTo(0),
                () -> assertThat(studyAttendanceRepository.getAbsenceStatusCount(studies[1].getId(), participants[2].getId())).isEqualTo(1),
                () -> assertThat(studyAttendanceRepository.getNonAttendanceStatusCount(studies[1].getId(), participants[2].getId())).isEqualTo(0),

                () -> assertThat(studyAttendanceRepository.getAttendanceStatusCount(studies[2].getId(), participants[2].getId())).isEqualTo(1),
                () -> assertThat(studyAttendanceRepository.getLateStatusCount(studies[2].getId(), participants[2].getId())).isEqualTo(1),
                () -> assertThat(studyAttendanceRepository.getAbsenceStatusCount(studies[2].getId(), participants[2].getId())).isEqualTo(0),
                () -> assertThat(studyAttendanceRepository.getNonAttendanceStatusCount(studies[2].getId(), participants[2].getId())).isEqualTo(0),

                () -> assertThat(scoreOfParticipant2).isEqualTo(previousScore + Score.ABSENCE * 2)
        );

        /**
         * none
         */
        final int scoreOfParticipant3 = memberRepository.getById(participants[3].getId()).getScore().getValue();
        assertAll(
                () -> assertThat(studyAttendanceRepository.getAttendanceStatusCount(studies[0].getId(), participants[3].getId())).isEqualTo(1),
                () -> assertThat(studyAttendanceRepository.getLateStatusCount(studies[0].getId(), participants[3].getId())).isEqualTo(1),
                () -> assertThat(studyAttendanceRepository.getAbsenceStatusCount(studies[0].getId(), participants[3].getId())).isEqualTo(0),
                () -> assertThat(studyAttendanceRepository.getNonAttendanceStatusCount(studies[0].getId(), participants[3].getId())).isEqualTo(0),

                () -> assertThat(studyAttendanceRepository.getAttendanceStatusCount(studies[1].getId(), participants[3].getId())).isEqualTo(1),
                () -> assertThat(studyAttendanceRepository.getLateStatusCount(studies[1].getId(), participants[3].getId())).isEqualTo(0),
                () -> assertThat(studyAttendanceRepository.getAbsenceStatusCount(studies[1].getId(), participants[3].getId())).isEqualTo(0),
                () -> assertThat(studyAttendanceRepository.getNonAttendanceStatusCount(studies[1].getId(), participants[3].getId())).isEqualTo(1),

                () -> assertThat(studyAttendanceRepository.getAttendanceStatusCount(studies[2].getId(), participants[3].getId())).isEqualTo(0),
                () -> assertThat(studyAttendanceRepository.getLateStatusCount(studies[2].getId(), participants[3].getId())).isEqualTo(1),
                () -> assertThat(studyAttendanceRepository.getAbsenceStatusCount(studies[2].getId(), participants[3].getId())).isEqualTo(0),
                () -> assertThat(studyAttendanceRepository.getNonAttendanceStatusCount(studies[2].getId(), participants[3].getId())).isEqualTo(1),

                () -> assertThat(scoreOfParticipant3).isEqualTo(previousScore)
        );

        /**
         * studies[0] - Week2
         * studies[2] - Week1
         */
        final int scoreOfParticipant4 = memberRepository.getById(participants[4].getId()).getScore().getValue();
        assertAll(
                () -> assertThat(studyAttendanceRepository.getAttendanceStatusCount(studies[0].getId(), participants[4].getId())).isEqualTo(0),
                () -> assertThat(studyAttendanceRepository.getLateStatusCount(studies[0].getId(), participants[4].getId())).isEqualTo(0),
                () -> assertThat(studyAttendanceRepository.getAbsenceStatusCount(studies[0].getId(), participants[4].getId())).isEqualTo(1),
                () -> assertThat(studyAttendanceRepository.getNonAttendanceStatusCount(studies[0].getId(), participants[4].getId())).isEqualTo(1),

                () -> assertThat(studyAttendanceRepository.getAttendanceStatusCount(studies[1].getId(), participants[4].getId())).isEqualTo(2),
                () -> assertThat(studyAttendanceRepository.getLateStatusCount(studies[1].getId(), participants[4].getId())).isEqualTo(0),
                () -> assertThat(studyAttendanceRepository.getAbsenceStatusCount(studies[1].getId(), participants[4].getId())).isEqualTo(0),
                () -> assertThat(studyAttendanceRepository.getNonAttendanceStatusCount(studies[1].getId(), participants[4].getId())).isEqualTo(0),

                () -> assertThat(studyAttendanceRepository.getAttendanceStatusCount(studies[2].getId(), participants[4].getId())).isEqualTo(0),
                () -> assertThat(studyAttendanceRepository.getLateStatusCount(studies[2].getId(), participants[4].getId())).isEqualTo(0),
                () -> assertThat(studyAttendanceRepository.getAbsenceStatusCount(studies[2].getId(), participants[4].getId())).isEqualTo(1),
                () -> assertThat(studyAttendanceRepository.getNonAttendanceStatusCount(studies[2].getId(), participants[4].getId())).isEqualTo(1),

                () -> assertThat(scoreOfParticipant4).isEqualTo(previousScore + Score.ABSENCE * 2)
        );
    }
}
