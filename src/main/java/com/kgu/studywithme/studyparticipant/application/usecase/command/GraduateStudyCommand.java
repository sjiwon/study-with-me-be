package com.kgu.studywithme.studyparticipant.application.usecase.command;

public record GraduateStudyCommand(
        Long studyId,
        Long participantId
) {
}
