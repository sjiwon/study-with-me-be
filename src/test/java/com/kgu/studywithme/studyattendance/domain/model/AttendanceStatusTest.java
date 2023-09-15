package com.kgu.studywithme.studyattendance.domain.model;

import com.kgu.studywithme.common.ParallelTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.ABSENCE;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.ATTENDANCE;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.LATE;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.NON_ATTENDANCE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("StudyAttendance -> 도메인 [AttendanceStatus VO] 테스트")
class AttendanceStatusTest extends ParallelTest {
    @Test
    @DisplayName("Description으로 AttendanceStatus를 조회한다")
    void fromDescription() {
        assertAll(
                () -> assertThat(AttendanceStatus.fromDescription("출석")).isEqualTo(ATTENDANCE),
                () -> assertThat(AttendanceStatus.fromDescription("지각")).isEqualTo(LATE),
                () -> assertThat(AttendanceStatus.fromDescription("결석")).isEqualTo(ABSENCE),
                () -> assertThat(AttendanceStatus.fromDescription("미출결")).isEqualTo(NON_ATTENDANCE),
                () -> assertThat(AttendanceStatus.fromDescription("????")).isEqualTo(NON_ATTENDANCE)
        );
    }

    @Test
    @DisplayName("AttendanceStatus의 Description 목록을 가져온다")
    void getAttendanceStatuses() {
        assertThat(AttendanceStatus.getAttendanceStatuses()).containsExactlyInAnyOrder(ATTENDANCE, LATE, ABSENCE, NON_ATTENDANCE);
    }
}
