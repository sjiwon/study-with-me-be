package com.kgu.studywithme.studyweekly.event;

public record AssignmentEditedEvent(
        Long studyId,
        Long weeklyId,
        Long participantId
) {
}
