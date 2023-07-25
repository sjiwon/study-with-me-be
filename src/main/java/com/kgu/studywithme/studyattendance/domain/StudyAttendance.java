package com.kgu.studywithme.studyattendance.domain;

import com.kgu.studywithme.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.ATTENDANCE;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "study_attendance")
public class StudyAttendance extends BaseEntity<StudyAttendance> {
    @Column(name = "study_id", nullable = false)
    private Long studyId;

    @Column(name = "participant_id", nullable = false)
    private Long participantId;

    @Column(name = "week", nullable = false)
    private int week;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AttendanceStatus status;

    private StudyAttendance(
            final Long studyId,
            final Long participantId,
            final int week,
            final AttendanceStatus status
    ) {
        this.studyId = studyId;
        this.participantId = participantId;
        this.week = week;
        this.status = status;
    }

    public static StudyAttendance recordAttendance(
            final Long studyId,
            final Long participantId,
            final int week,
            final AttendanceStatus status
    ) {
        return new StudyAttendance(studyId, participantId, week, status);
    }

    public void updateAttendanceStatus(final AttendanceStatus status) {
        this.status = status;
    }

    public boolean isAttendanceStatus() {
        return status == ATTENDANCE;
    }
}
