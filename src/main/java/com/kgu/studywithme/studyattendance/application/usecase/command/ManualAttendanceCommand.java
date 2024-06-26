package com.kgu.studywithme.studyattendance.application.usecase.command;

import com.kgu.studywithme.studyattendance.domain.model.AttendanceStatus;

public record ManualAttendanceCommand(
        Long studyId,
        Long participantId,
        int week,
        AttendanceStatus attendanceStatus
) {
}
