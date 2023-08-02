package com.kgu.studywithme.studyattendance.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum AttendanceStatus {
    ATTENDANCE("출석"),
    LATE("지각"),
    ABSENCE("결석"),
    NON_ATTENDANCE("미출결"),
    ;

    private final String description;

    public static AttendanceStatus fromDescription(final String description) {
        return Arrays.stream(values())
                .filter(status -> status.getDescription().equals(description))
                .findFirst()
                .orElse(NON_ATTENDANCE);
    }

    public static List<AttendanceStatus> getAttendanceStatuses() {
        return Arrays.stream(values())
                .sorted(Comparator.comparingInt(Enum::ordinal))
                .toList();
    }
}
