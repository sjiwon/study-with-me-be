package com.kgu.studywithme.studyparticipant.event;

public record StudyApprovedEvent(
        String email,
        String nickname,
        String studyName
) {
}
