package com.kgu.studywithme.studyparticipant.application.usecase.command;

public record ApplyStudyCommand(
        Long studyId,
        Long applierId
) {
}
