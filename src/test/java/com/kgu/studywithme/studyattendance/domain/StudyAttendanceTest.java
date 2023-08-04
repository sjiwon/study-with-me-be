package com.kgu.studywithme.studyattendance.domain;

import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.kgu.studywithme.common.fixture.MemberFixture.JIWON;
import static com.kgu.studywithme.common.fixture.StudyFixture.SPRING;
import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.ATTENDANCE;
import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.LATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("StudyAttendance -> 도메인 [StudyAttendance] 테스트")
class StudyAttendanceTest {
    private final Member member = JIWON.toMember().apply(1L, LocalDateTime.now());
    private final Study study = SPRING.toOnlineStudy(member.getId()).apply(1L, LocalDateTime.now());

    @Test
    @DisplayName("StudyAttendance의 status를 변경한다")
    void updateStudyAttendanceStatus() {
        // given
        final StudyAttendance attendance = StudyAttendance.recordAttendance(
                study.getId(),
                member.getId(),
                1,
                ATTENDANCE
        );

        // when
        attendance.updateAttendanceStatus(LATE);

        // then
        assertThat(attendance.getStatus()).isEqualTo(LATE);
    }

    @Test
    @DisplayName("StudyAttendance의 status가 출석 상태(ATTENDANCE)인지 확인한다")
    void isStudyAttendanceStatus() {
        // given
        final StudyAttendance attendance1 = StudyAttendance.recordAttendance(
                study.getId(),
                member.getId(),
                1,
                ATTENDANCE
        );
        final StudyAttendance attendance2 = StudyAttendance.recordAttendance(
                study.getId(),
                member.getId(),
                1,
                LATE
        );

        // when
        final boolean actual1 = attendance1.isAttendanceStatus();
        final boolean actual2 = attendance2.isAttendanceStatus();

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}
