package com.kgu.studywithme.studyparticipant.application.usecase.command;

public record DelegateHostAuthorityCommand(
        Long studyId,
        Long newHostId
) {
}
