package com.kgu.studywithme.studyweekly.event;

public record WeeklyCreatedEvent(
        Long studyId,
        int week
) {
}
