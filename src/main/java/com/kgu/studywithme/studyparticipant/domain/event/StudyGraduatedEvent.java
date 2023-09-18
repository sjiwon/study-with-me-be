package com.kgu.studywithme.studyparticipant.domain.event;

public record StudyGraduatedEvent(
        String email,
        String nickname,
        String studyName
) {
}
