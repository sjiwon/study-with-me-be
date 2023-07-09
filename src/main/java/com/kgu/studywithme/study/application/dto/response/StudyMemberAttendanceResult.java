package com.kgu.studywithme.study.application.dto.response;

import java.util.List;

public record StudyMemberAttendanceResult(
        StudyMember member,
        List<AttendanceSummary> summaries
) {
}
