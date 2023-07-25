package com.kgu.studywithme.studyparticipant.event;

public record StudyRejectedEvent(
        String email,
        String nickname,
        String studyName,
        String reason
) {
}
