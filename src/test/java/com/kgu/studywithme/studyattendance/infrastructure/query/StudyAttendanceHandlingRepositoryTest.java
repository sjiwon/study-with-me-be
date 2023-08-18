package com.kgu.studywithme.studyattendance.infrastructure.query;

import com.kgu.studywithme.common.RepositoryTest;
import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.member.infrastructure.persistence.MemberJpaRepository;
import com.kgu.studywithme.study.domain.Study;
import com.kgu.studywithme.study.infrastructure.persistence.StudyJpaRepository;
import com.kgu.studywithme.studyattendance.domain.StudyAttendance;
import com.kgu.studywithme.studyattendance.infrastructure.persistence.StudyAttendanceJpaRepository;
import com.kgu.studywithme.studyattendance.infrastructure.query.dto.NonAttendanceWeekly;
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
import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.ATTENDANCE;
import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.LATE;
import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.NON_ATTENDANCE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(StudyAttendanceHandlingRepository.class)
@DisplayName("StudyAttendance -> StudyAttendanceHandlingRepository 테스트")
class StudyAttendanceHandlingRepositoryTest extends RepositoryTest {
    @Autowired
    private StudyAttendanceHandlingRepository studyAttendanceHandlingRepository;

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
    @DisplayName("사용자의 스터디 출석 횟수를 조회한다")
    void getAttendanceCount() {
        /* 출석 1회 */
        studyAttendanceJpaRepository.save(
                StudyAttendance.recordAttendance(study.getId(), member[0].getId(), 1, ATTENDANCE)
        );
        assertThat(studyAttendanceHandlingRepository.getAttendanceCount(study.getId(), member[0].getId())).isEqualTo(1);

        /* 출석 1회 + 지각 1회 */
        studyAttendanceJpaRepository.save(
                StudyAttendance.recordAttendance(study.getId(), member[0].getId(), 2, LATE)
        );
        assertThat(studyAttendanceHandlingRepository.getAttendanceCount(study.getId(), member[0].getId())).isEqualTo(1);

        /* 출석 2회 + 지각 1회 */
        studyAttendanceJpaRepository.save(
                StudyAttendance.recordAttendance(study.getId(), member[0].getId(), 3, ATTENDANCE)
        );
        assertThat(studyAttendanceHandlingRepository.getAttendanceCount(study.getId(), member[0].getId())).isEqualTo(2);
    }

    @Test
    @DisplayName("미출석 주차 정보들을 조회한다")
    void findNonAttendanceInformation() {
        /* 1주차 */
        studyAttendanceJpaRepository.saveAll(
                List.of(
                        StudyAttendance.recordAttendance(study.getId(), member[0].getId(), 1, NON_ATTENDANCE),
                        StudyAttendance.recordAttendance(study.getId(), member[1].getId(), 1, NON_ATTENDANCE),
                        StudyAttendance.recordAttendance(study.getId(), member[2].getId(), 1, ATTENDANCE),
                        StudyAttendance.recordAttendance(study.getId(), member[3].getId(), 1, ATTENDANCE),
                        StudyAttendance.recordAttendance(study.getId(), member[4].getId(), 1, NON_ATTENDANCE)
                )
        );

        final List<NonAttendanceWeekly> result1 = studyAttendanceHandlingRepository.findNonAttendanceInformation();
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
        studyAttendanceJpaRepository.saveAll(
                List.of(
                        StudyAttendance.recordAttendance(study.getId(), member[0].getId(), 2, ATTENDANCE),
                        StudyAttendance.recordAttendance(study.getId(), member[1].getId(), 2, NON_ATTENDANCE),
                        StudyAttendance.recordAttendance(study.getId(), member[2].getId(), 2, ATTENDANCE),
                        StudyAttendance.recordAttendance(study.getId(), member[3].getId(), 2, NON_ATTENDANCE),
                        StudyAttendance.recordAttendance(study.getId(), member[4].getId(), 2, ATTENDANCE)
                )
        );

        final List<NonAttendanceWeekly> result2 = studyAttendanceHandlingRepository.findNonAttendanceInformation();
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
