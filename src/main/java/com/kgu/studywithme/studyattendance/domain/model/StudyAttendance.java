package com.kgu.studywithme.studyattendance.domain.model;

import com.kgu.studywithme.global.BaseEntity;
import com.kgu.studywithme.member.domain.model.Member;
import com.kgu.studywithme.study.domain.model.Study;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.ATTENDANCE;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "study_attendance",
        indexes = {
                @Index(name = "idx_study_attendance_participant_id_status", columnList = "participant_id, status"),
                @Index(name = "idx_study_attendance_study_id_participant_id_status", columnList = "study_id, participant_id, status"),
                @Index(name = "idx_study_attendance_study_id_week", columnList = "study_id, week"),
                @Index(name = "idx_study_attendance_status_study_id_week_participant_id", columnList = "status, study_id, week, participant_id")
        }
)
public class StudyAttendance extends BaseEntity<StudyAttendance> {
    @Column(name = "week", nullable = false)
    private int week;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AttendanceStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "study_id", referencedColumnName = "id", nullable = false)
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "participant_id", referencedColumnName = "id", nullable = false)
    private Member participant;

    private StudyAttendance(
            final Study study,
            final Member participant,
            final int week,
            final AttendanceStatus status
    ) {
        this.study = study;
        this.participant = participant;
        this.week = week;
        this.status = status;
    }

    public static StudyAttendance recordAttendance(
            final Study study,
            final Member participant,
            final int week,
            final AttendanceStatus status
    ) {
        return new StudyAttendance(study, participant, week, status);
    }

    public void updateAttendanceStatus(final AttendanceStatus status) {
        this.status = status;
    }

    public boolean isAttendanceStatus() {
        return status == ATTENDANCE;
    }
}
