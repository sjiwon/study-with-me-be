package com.kgu.studywithme.studyattendance.presentation.dto.request;

import com.kgu.studywithme.studyattendance.utils.validator.AttendanceStatusUpdateConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ManualAttendanceRequest(
        @NotNull(message = "스터디 주차는 필수입니다.")
        Integer week,

        @NotBlank(message = "출석 상태는 필수입니다.")
        @AttendanceStatusUpdateConstraint
        String status
) {
}
