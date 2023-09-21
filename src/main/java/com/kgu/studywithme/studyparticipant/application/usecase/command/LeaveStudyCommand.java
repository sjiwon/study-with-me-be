package com.kgu.studywithme.studyparticipant.application.usecase.command;

public record LeaveStudyCommand(
        Long studyId,
        Long participantId
) {
}
