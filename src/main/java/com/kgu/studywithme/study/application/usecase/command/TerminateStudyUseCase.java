package com.kgu.studywithme.study.application.usecase.command;

public interface TerminateStudyUseCase {
    void terminateStudy(final Command command);

    record Command(
            Long studyId
    ) {
    }
}
