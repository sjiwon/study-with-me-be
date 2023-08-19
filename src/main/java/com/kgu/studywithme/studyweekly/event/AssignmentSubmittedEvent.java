package com.kgu.studywithme.studyweekly.event;

public record AssignmentSubmittedEvent(
        Long studyId,
        Long weeklyId,
        Long participantId
) {
}
