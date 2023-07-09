package com.kgu.studywithme.study.application.dto.response;

import java.util.List;

public record AttendanceAssmbler(
        List<StudyMemberAttendanceResult> result
) {
}
