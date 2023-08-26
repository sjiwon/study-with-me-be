package com.kgu.studywithme.dummydata;

import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.ATTENDANCE;
import static com.kgu.studywithme.studyattendance.domain.AttendanceStatus.LATE;

public record DummyStudyAttendance(
        long studyId, int week,
        long participantId, String status
) {
    public DummyStudyAttendance(final long studyId, final long participantId) {
        this(
                studyId,
                1,
                participantId,
                (participantId % 5 == 0) ? LATE.name() : ATTENDANCE.name()
        );
    }
}
