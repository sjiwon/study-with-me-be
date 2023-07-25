package com.kgu.studywithme.studyparticipant.event;

public record StudyGraduatedEvent(
        String email,
        String nickname,
        String studyName
) {
}
