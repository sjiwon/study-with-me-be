package com.kgu.studywithme.studyparticipant.application.usecase.command;

public record RejectParticipationCommand(
        Long studyId,
        Long applierId,
        String reason
) {
}
