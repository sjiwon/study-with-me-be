package com.kgu.studywithme.studyattendance.domain.model;

import com.kgu.studywithme.common.ParallelTest;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.domain.model.Study;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.ABSENCE;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.ATTENDANCE;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.LATE;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.NON_ATTENDANCE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("StudyAttendance -> 도메인 [StudyAttendance] 테스트")
class StudyAttendanceTest extends ParallelTest {
    private final Member member = JIWON.toMember().apply(1L);
    private final Study study = SPRING.toStudy(member).apply(1L);

    @Test
    @DisplayName("StudyAttendance의 status를 변경한다")
    void updateAttendanceStatus() {
        // given
        final StudyAttendance attendance = StudyAttendance.recordAttendance(study, member, 1, ATTENDANCE);

        // when
        attendance.updateAttendanceStatus(LATE);

        // then
        assertThat(attendance.getStatus()).isEqualTo(LATE);
    }

    @Test
    @DisplayName("StudyAttendance의 status가 출석 상태(ATTENDANCE)인지 확인한다")
    void isAttendanceStatus() {
        // given
        final StudyAttendance attendance1 = StudyAttendance.recordAttendance(study, member, 1, ATTENDANCE);
        final StudyAttendance attendance2 = StudyAttendance.recordAttendance(study, member, 1, LATE);
        final StudyAttendance attendance3 = StudyAttendance.recordAttendance(study, member, 1, ABSENCE);
        final StudyAttendance attendance4 = StudyAttendance.recordAttendance(study, member, 1, NON_ATTENDANCE);

        // when
        final boolean actual1 = attendance1.isAttendanceStatus();
        final boolean actual2 = attendance2.isAttendanceStatus();
        final boolean actual3 = attendance3.isAttendanceStatus();
        final boolean actual4 = attendance4.isAttendanceStatus();

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse(),
                () -> assertThat(actual3).isFalse(),
                () -> assertThat(actual4).isFalse()
        );
    }
}
