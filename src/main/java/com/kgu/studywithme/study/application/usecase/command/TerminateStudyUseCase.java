package com.kgu.studywithme.study.application.usecase.command;

public interface TerminateStudyUseCase {
    void invoke(final Command command);

    record Command(
            Long studyId
    ) {
    }
}
