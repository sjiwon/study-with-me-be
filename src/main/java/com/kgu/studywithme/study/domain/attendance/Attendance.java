package com.kgu.studywithme.study.domain.attendance;

import com.kgu.studywithme.member.domain.Member;
import com.kgu.studywithme.study.domain.Study;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.kgu.studywithme.study.domain.attendance.AttendanceStatus.ATTENDANCE;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "study_attendance")
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AttendanceStatus status;

    @Column(name = "week", nullable = false)
    private int week;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "study_id", referencedColumnName = "id", nullable = false)
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "participant_id", referencedColumnName = "id", nullable = false)
    private Member participant;

    private Attendance(
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

    public static Attendance recordAttendance(
            final Study study,
            final Member participant,
            final int week,
            final AttendanceStatus status
    ) {
        return new Attendance(study, participant, week, status);
    }

    public void updateAttendanceStatus(final AttendanceStatus status) {
        this.status = status;
    }

    public boolean isAttendanceStatus() {
        return status == ATTENDANCE;
    }
}
