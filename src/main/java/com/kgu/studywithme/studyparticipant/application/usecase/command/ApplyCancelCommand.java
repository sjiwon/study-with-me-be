package com.kgu.studywithme.studyparticipant.application.usecase.command;

public record ApplyCancelCommand(
        Long studyId,
        Long applierId
) {
}
