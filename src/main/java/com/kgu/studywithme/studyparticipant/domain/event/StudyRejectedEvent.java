package com.kgu.studywithme.studyparticipant.domain.event;

public record StudyRejectedEvent(
        String email,
        String nickname,
        String studyName,
        String reason
) {
}
