package com.kgu.studywithme.studyparticipant.domain.event;

public record StudyApprovedEvent(
        String email,
        String nickname,
        String studyName
) {
}
