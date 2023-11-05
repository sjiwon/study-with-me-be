package com.kgu.studywithme.dummydata;

import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.ATTENDANCE;
import static com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus.LATE;

public record DummyStudyAttendance(
        long studyId, int week,
        long participantId, String status
) {
    public DummyStudyAttendance(final long studyId, final long participantId) {
        this(
                studyId,
                1,
                participantId,
                (participantId % 3 == 0) ? LATE.name() : ATTENDANCE.name()
        );
    }
}
