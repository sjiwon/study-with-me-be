package com.kgu.studywithme.studyattendance.application.usecase.command;

import com.kgu.studywithme.studyattendance.domain.AttendanceStatus;

public interface ManualAttendanceUseCase {
    void invoke(final Command command);

    record Command(
            Long studyId,
            Long participantId,
            int week,
            AttendanceStatus attendanceStatus
    ) {
    }
}
