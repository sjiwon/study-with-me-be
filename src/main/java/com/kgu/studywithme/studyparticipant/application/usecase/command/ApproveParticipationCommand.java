package com.kgu.studywithme.studyparticipant.application.usecase.command;

public record ApproveParticipationCommand(
        Long studyId,
        Long applierId
) {
}
